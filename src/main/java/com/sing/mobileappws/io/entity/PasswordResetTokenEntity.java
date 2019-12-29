package com.sing.mobileappws.io.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Data
@Entity(name = "password_reset_tokens")
public class PasswordResetTokenEntity implements Serializable {

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private static final long serialVersionUID = 8051324316462829780L;

	@Id
	@GeneratedValue
	private long id;

	private String token;

	@OneToOne()
	@JoinColumn(name = "users_id")
	private UserEntity userDetails;

}
