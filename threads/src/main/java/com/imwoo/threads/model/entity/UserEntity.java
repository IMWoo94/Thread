package com.imwoo.threads.model.entity;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Random;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
/**
 * user 앞 뒤로 쌍따옴표를 붙여서 사용해야 한다.
 * 그 이유는 postgreSQL 에서는 user 라는 예약어가 존재한다.
 * 예약어를 사용하는 것이 아닌 user 라는 단어를 사용한다는 것은 인식하기 위해 적용
 */
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SQLDelete(sql = "update users set deletedDateTime = CURRENT_TIMESTAMP where userId = ?")
//@Where(clause = "deletedDateTime IS NULL")
@SQLRestriction("deletedDateTime IS NULL")
/**
 * Spring Security 에서 사용자 인증에 사용되는 User 정보를 담고 있는 UserDetails 를 상속
 * 별도의 UserDetails 를 적용하지 않으면 DaoAuthenticationProvider 에서 InMemoryUserDetailsManager 를 통해
 * org.springframework.security.core.userdetails.user 를 가져온다.
 * 사용자 정의된 UserDetails 를 사용할 수 있도록 userDetails 를 설정 상에서 변경 해주어야 한다.
 */
public class UserEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column
	private String profile;

	@Column
	private String description;

	@Column(nullable = false, updatable = false)
	@CreatedDate
	private ZonedDateTime createdDateTime;

	@Column(nullable = false)
	@LastModifiedDate
	private ZonedDateTime updatedDateTime;

	@Column
	private ZonedDateTime deletedDateTime;

	public static UserEntity of(String username, String password) {
		var user = new UserEntity();
		user.setUsername(username);
		user.setPassword(password);

		// Avatar Placeholder 서비스 (https://avatar-placeholder.iran.liara.run/) 기반
		// 랜덤한 프로필 사진 설정 (1~100)
		user.setProfile("https://avatar-placeholder.iran.liara.run/public/" + new Random().nextInt(100) + 1);

		return user;
	}

	@PrePersist
	private void prePersist() {
		this.createdDateTime = ZonedDateTime.now();
		this.updatedDateTime = this.createdDateTime;
	}

	@PreUpdate
	private void preUpdate() {
		this.updatedDateTime = ZonedDateTime.now();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO 사용자별 역할 구분 ( admin, user )
		return null;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}
}
