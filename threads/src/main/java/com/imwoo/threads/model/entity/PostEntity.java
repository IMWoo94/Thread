package com.imwoo.threads.model.entity;

import java.time.ZonedDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SQLDelete(sql = "update post set deletedDateTime = CURRENT_TIMESTAMP where postId = ?")
/**
 * @Deprecated(
 *    since = "6.3"
 *	)
 *    @Where(clause = "deletedDateTime IS NULL")
 **/
@SQLRestriction("deletedDateTime IS NULL")
public class PostEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postId;

	@Column(columnDefinition = "TEXT")
	private String body;

	@Column(nullable = false, updatable = false)
	@CreatedDate
	private ZonedDateTime createdDateTime;

	@Column(nullable = false)
	@LastModifiedDate
	private ZonedDateTime updatedDateTime;

	@Column
	private ZonedDateTime deletedDateTime;

	@PrePersist
	private void prePersist() {
		this.createdDateTime = ZonedDateTime.now();
		this.updatedDateTime = this.createdDateTime;
	}

	@PreUpdate
	private void preUpdate() {
		this.updatedDateTime = ZonedDateTime.now();
	}
}
