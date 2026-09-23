package negocio;

import java.io.Serializable;

public class Login implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4648181854244228568L;
	private String usuario;
    private String password;

    public Login(){
    }

    public Login(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    public String getUsuario() { return usuario; }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }
}
