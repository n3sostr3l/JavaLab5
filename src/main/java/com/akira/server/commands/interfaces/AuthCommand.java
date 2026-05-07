package com.akira.server.commands.interfaces;

/**
 * Маркерный интерфейс для команд, требующих аутентификацию.
 */
public interface AuthCommand extends Command {
	/**
	 * Устанавливает хеш пароля для выполнения аутентифицированной операции.
	 * @param passwordHash hex-хеш пароля (SHA-224)
	 */
	default void setPasswordHash(String passwordHash) {}
}
