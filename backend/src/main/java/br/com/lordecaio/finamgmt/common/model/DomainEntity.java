package br.com.lordecaio.finamgmt.common.model;

import br.com.lordecaio.finamgmt.common.enums.EntityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

@MappedSuperclass
public abstract class DomainEntity<T extends DomainEntity<T>> extends AuditableEntity<T> {

	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_DELETED_AT = "deleted_at";

	private static final Logger logger = LoggerFactory.getLogger(DomainEntity.class);

	@Enumerated(EnumType.STRING)
	@Column(name = DomainEntity.COLUMN_STATUS, nullable = false)
	private EntityStatus status;

	@Column(name = DomainEntity.COLUMN_DELETED_AT)
	private OffsetDateTime deletedAt;

	@Transient
	private transient boolean disallowHibernateExclusion;

	protected DomainEntity() {
		super();
		this.status = EntityStatus.ACTIVE;
		this.disallowHibernateExclusion = true;
	}

	@PreUpdate
	protected void handlePreUpdate() {
		if (isDeleted() && disallowHibernateExclusion) {
			logger.error("Security Breach: Attempted to update a DELETED {} [disallowHibernateExclusion is TRUE]",
				  this.getClass().getSimpleName());
			throw new IllegalStateException("Entity is immutable in DELETED state. Exclusion is disallowed.");
		}
	}

	@PreRemove
	protected void blockPhysicalDeletion() {
		String entityName = this.getClass().getSimpleName();
		logger.error("Security Violation: Physical deletion attempted on {} entity.", entityName);

		throw new IllegalStateException(
			  String.format("Physical deletion of [%s] is prohibited. Use the .delete() method for Soft Delete.", entityName)
		);
	}

	public final boolean isActive() { return EntityStatus.ACTIVE.equals(status); }

	public final boolean isInactive() { return EntityStatus.INACTIVE.equals(status); }

	public final boolean isDeleted() { return EntityStatus.DELETED.equals(status); }

	public final T activate() {
		if (isDeleted()) {
			logger.error("Constraint Violation: Cannot activate a DELETED {} entity", this.getClass().getSimpleName());
			return self();
		}
		this.status = EntityStatus.ACTIVE;
		return self();
	}

	public final T deactivate() {
		if (isDeleted()) {
			logger.error("Constraint Violation: Cannot deactivate a DELETED {} entity", this.getClass().getSimpleName());
			return self();
		}
		this.status = EntityStatus.INACTIVE;
		return self();
	}

	public final T delete() {
		if (isDeleted()) {
			return self();
		}

		if (this.isNeo()) {
			logger.warn("Operation Aborted: {} is new. Cannot kill what never lived.",
				  this.getClass().getSimpleName());
			return self();
		}

		this.disallowHibernateExclusion = false;
		this.status = EntityStatus.DELETED;
		this.deletedAt = OffsetDateTime.now();

		logger.info("Soft-delete executed for {} at {}",
			  this.getClass().getSimpleName(), this.deletedAt);

		return self();
	}

	public EntityStatus getStatus() {
		return status;
	}

	public OffsetDateTime getDeletedAt() {
		return deletedAt;
	}
}