package br.com.lordecaio.finamgmt.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@MappedSuperclass
public abstract class AuditableEntity<T extends AuditableEntity<T>> {

	public static final String COLUMN_CREATED_AT = "created_at";
	public static final String COLUMN_UPDATED_AT = "updated_at";

	@CreationTimestamp
	@Column(name = AuditableEntity.COLUMN_CREATED_AT, updatable = false, nullable = false)
	private OffsetDateTime createdAt;

	@UpdateTimestamp
	@Column(name = AuditableEntity.COLUMN_UPDATED_AT, nullable = false)
	private OffsetDateTime updatedAt;

	@Transient
	private transient boolean neo;

	protected AuditableEntity() {
		this.neo = true;
	}

	@PostLoad
	@PostPersist
	protected void markAsNotNeo() {
		this.neo = false;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public T withCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
		return this.self();
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}

	public T withUpdatedAt(OffsetDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this.self();
	}

	protected final boolean isNeo() {
		return neo;
	}

	@SuppressWarnings("unchecked")
	final T self() {
		return (T) this;
	}
}
