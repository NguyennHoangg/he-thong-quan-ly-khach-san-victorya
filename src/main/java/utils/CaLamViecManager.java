package utils;

public class CaLamViecManager {
    private static CaLamViecManager instance;
    private String currentUser;
    private String currentRole;
    
    private CaLamViecManager() {
    }
    
    public static CaLamViecManager getInstance() {
        if (instance == null) {
            instance = new CaLamViecManager();
        }
        return instance;
    }
    
    public void setCurrentUser(String username) {
        this.currentUser = username;
    }
    
    public String getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentRole(String role) {
        this.currentRole = role;
    }
    
    public String getCurrentRole() {
        return currentRole;
    }
    
    public void clearSession() {
        currentUser = null;
        currentRole = null;
    }
}
