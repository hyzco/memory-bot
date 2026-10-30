package com.github.manolo8.darkbot.gui.login;

import com.github.manolo8.darkbot.utils.I18n;
import com.github.manolo8.darkbot.utils.login.LoginData;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.function.Consumer;

public class SidLogin extends JPanel implements LoginScreen {
    private final JTextField sv = new JTextField(4), sid = new JTextField(16);

    public SidLogin() {
        super(new MigLayout("wrap 1, fillx, ins 16 8 8 8", "[grow,fill]", "[]10[]"));
        sv.putClientProperty("JTextField.placeholderText", I18n.get("gui.login.sid.server"));
        sid.putClientProperty("JTextField.placeholderText", I18n.get("gui.login.sid.sid"));
        add(sv, "h 38!");
        add(sid, "h 38!");
    }

    @Override
    public LoginForm.Message tryLogin(LoginData login, Consumer<LoginForm.Message> publish) {
        login.setSid(sid.getText(), sv.getText() + ".darkorbit.com");
        return null;
    }
}
