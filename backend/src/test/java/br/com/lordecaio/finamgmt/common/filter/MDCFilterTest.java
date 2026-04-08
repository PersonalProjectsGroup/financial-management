package br.com.lordecaio.finamgmt.common.filter;

import br.com.lordecaio.finamgmt.common.util.UUIDUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MDCFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private MDCFilter mdcFilter;

    private MockedStatic<MDC> mockedMDC;
    private MockedStatic<UUIDUtils> mockedUUIDUtils;

    @BeforeEach
    void setUp() {
        mockedMDC = Mockito.mockStatic(MDC.class);
        mockedUUIDUtils = Mockito.mockStatic(UUIDUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedMDC.close();
        mockedUUIDUtils.close();
    }

    @Test
    void shouldUseExistingTraceIdFromHeader() throws ServletException, IOException {
        String existingTraceId = "T3E-existing-trace-id";
        when(request.getHeader(MDCFilter.TRACE_ID_HEADER)).thenReturn(existingTraceId);

        mdcFilter.doFilterInternal(request, response, filterChain);

        mockedMDC.verify(() -> MDC.put(MDCFilter.TRACE_ID_KEY, existingTraceId), times(1));
        verify(response, times(1)).addHeader(MDCFilter.TRACE_ID_HEADER, existingTraceId);
        verify(filterChain, times(1)).doFilter(request, response);
        mockedMDC.verify(() -> MDC.remove(MDCFilter.TRACE_ID_KEY), times(1));
        mockedUUIDUtils.verify(UUIDUtils::nextV7, never());
    }

    @Test
    void shouldGenerateNewTraceIdWhenHeaderIsMissing() throws ServletException, IOException {
        String generatedUuid = "0123456789abcdef0123456789abcdef";
        UUID mockUuid = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");
        String expectedTraceId = String.format(MDCFilter.TRACE_ID_PATTERN, generatedUuid);

        when(request.getHeader(MDCFilter.TRACE_ID_HEADER)).thenReturn(null);
        mockedUUIDUtils.when(UUIDUtils::nextV7).thenReturn(mockUuid);

        mdcFilter.doFilterInternal(request, response, filterChain);

        mockedUUIDUtils.verify(UUIDUtils::nextV7, times(1));
        mockedMDC.verify(() -> MDC.put(MDCFilter.TRACE_ID_KEY, expectedTraceId), times(1));
        verify(response, times(1)).addHeader(MDCFilter.TRACE_ID_HEADER, expectedTraceId);
        verify(filterChain, times(1)).doFilter(request, response);
        mockedMDC.verify(() -> MDC.remove(MDCFilter.TRACE_ID_KEY), times(1));
    }

    @Test
    void shouldGenerateNewTraceIdWhenHeaderIsEmpty() throws ServletException, IOException {
        String generatedUuid = "fedcba9876543210fedcba9876543210";
        UUID mockUuid = UUID.fromString("fedcba98-7654-3210-fedc-ba9876543210");
        String expectedTraceId = String.format(MDCFilter.TRACE_ID_PATTERN, generatedUuid);

        when(request.getHeader(MDCFilter.TRACE_ID_HEADER)).thenReturn("");
        mockedUUIDUtils.when(UUIDUtils::nextV7).thenReturn(mockUuid);

        mdcFilter.doFilterInternal(request, response, filterChain);

        mockedUUIDUtils.verify(UUIDUtils::nextV7, times(1));
        mockedMDC.verify(() -> MDC.put(MDCFilter.TRACE_ID_KEY, expectedTraceId), times(1));
        verify(response, times(1)).addHeader(MDCFilter.TRACE_ID_HEADER, expectedTraceId);
        verify(filterChain, times(1)).doFilter(request, response);
        mockedMDC.verify(() -> MDC.remove(MDCFilter.TRACE_ID_KEY), times(1));
    }

    @Test
    void shouldAlwaysRemoveTraceIdFromMDC() throws ServletException, IOException {
        String existingTraceId = "T3E-test-trace-id";
        when(request.getHeader(MDCFilter.TRACE_ID_HEADER)).thenReturn(existingTraceId);
        doThrow(new IOException("Simulated filter chain error")).when(filterChain).doFilter(request, response);

        try {
            mdcFilter.doFilterInternal(request, response, filterChain);
        } catch (IOException e) {
            assertEquals("Simulated filter chain error", e.getMessage());
        }

        mockedMDC.verify(() -> MDC.put(MDCFilter.TRACE_ID_KEY, existingTraceId), times(1));
        verify(response, times(1)).addHeader(MDCFilter.TRACE_ID_HEADER, existingTraceId);
        verify(filterChain, times(1)).doFilter(request, response);
        mockedMDC.verify(() -> MDC.remove(MDCFilter.TRACE_ID_KEY), times(1));
    }
}
