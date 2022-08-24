package utilities.security;

/**
 * Clase con el contexto del usuario,
 * es lo que se manda en los JSON Web Token (JWT).
 */
public class UserInfoContext {
  public Long userId;
  public String username;
  public String ipAddress;
  public Long groupId;

  public UserInfoContext(Long userId, String username, String ipAddress, Long groupId) {
    this.userId = userId;
    this.username = username;
    this.ipAddress = ipAddress;
    this.groupId = groupId;
  }

  public String toString() {
    return String.format("userId: %s, username: %s, groupId: %s", userId, username, groupId);
  }
}
