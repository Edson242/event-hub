package com.edson.eventHub.dto;

import lombok.Data;

/**
 * Objeto para transportar os dados necessários para a alteração de senha.
 */
@Data
public class PasswordChangeRequestDTO {
    private String oldPassword;
    private String newPassword;
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
