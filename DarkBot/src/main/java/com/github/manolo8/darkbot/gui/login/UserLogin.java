package com.github.manolo8.darkbot.gui.login;

import com.github.manolo8.darkbot.utils.I18n;
import com.github.manolo8.darkbot.utils.login.LoginData;
import com.github.manolo8.darkbot.utils.login.LoginUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.function.Consumer;

public class UserLogin extends JPanel implements LoginScreen {
    private final JTextField username = new JTextField(16), password = new JPasswordField(16);

    public UserLogin() {
        super(new MigLayout("wrap 1, fillx, ins 16 8 8 8", "[grow,fill]", "[]10[]"));
        username.putClientProperty("JTextField.placeholderText", I18n.get("gui.login.user_pass.username"));
        password.putClientProperty("JTextField.placeholderText", I18n.get("gui.login.user_pass.password"));
        password.putClientProperty("JTextField.showRevealButton", true);
        add(username, "h 38!");
        add(password, "h 38!");
    }

    public UserLogin(String username, String password) {
        this();
        this.username.setText(username);
        this.password.setText(password);
    }

    public String getUsername() {
        return username.getText();
    }

    public String getPassword() {
        return password.getText();
    }

    @Override
    public LoginForm.Message tryLogin(LoginData login, Consumer<LoginForm.Message> publish) {
        login.setCredentials(username.getText(), password.getText(), null);
        LoginUtils.usernameLogin(login);
        return null;
    }

}
