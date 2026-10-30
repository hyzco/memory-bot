package com.github.manolo8.darkbot.gui.login;

import com.github.manolo8.darkbot.extensions.plugins.IssueHandler;
import com.github.manolo8.darkbot.gui.utils.UIUtils;
import com.github.manolo8.darkbot.utils.I18n;
import com.github.manolo8.darkbot.utils.login.LoginData;
import com.github.manolo8.darkbot.utils.login.LoginUtils;
import net.miginfocom.swing.MigLayout;

import javax.net.ssl.SSLHandshakeException;
import javax.swing.*;
import java.util.List;

public class LoginForm extends JPanel {

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final JLabel infoLb = new JLabel("");
    private final JButton loginBtn = new JButton(I18n.get("gui.login.log_in.button"));

    private final LoginData loginData = new LoginData();

    public LoginForm() {
        super(new MigLayout("wrap 1, ins 8 10 10 10, fillx", "[grow,fill,360:360:]", ""));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.putClientProperty("JTabbedPane.tabAreaAlignment", "fill");

        tabbedPane.addTab(I18n.get("gui.login.user_pass"), new UserLogin());
        tabbedPane.addTab(I18n.get("gui.login.sid"), new SidLogin());
        SavedLogins saved =  new SavedLogins(this);
        tabbedPane.addTab(I18n.get("gui.login.saved"), saved);
        tabbedPane.setEnabledAt(2, saved.isLoaded());
        if (saved.getLogins() > 0) tabbedPane.setSelectedComponent(saved);
        String lastUser = saved.selectLastUsed();

        loginBtn.addActionListener(ac -> startLogin());

        JLabel logo = new JLabel(new ImageIcon(com.github.manolo8.darkbot.gui.utils.AppIcon.create(44)));
        JLabel title = new JLabel("HAK CLAN");
        title.setFont(title.getFont().deriveFont(java.awt.Font.BOLD, 20f));
        title.setForeground(new java.awt.Color(0x8B5CF6));
        JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setForeground(new java.awt.Color(0x8A90A8));
        JPanel header = new JPanel(new MigLayout("ins 0, gapx 12", "[][]", "[]0[]"));
        header.setOpaque(false);
        header.add(logo, "spany 2");
        header.add(title, "wrap");
        header.add(subtitle);

        loginBtn.putClientProperty("FlatLaf.style",
                "background: #8B5CF6; foreground: #FFFFFF; hoverBackground: #A78BFA; pressedBackground: #7C3AED; "
                        + "arc: 14; font: bold +1; borderWidth: 0; focusWidth: 0");
        infoLb.setFont(infoLb.getFont().deriveFont(12f));

        add(header, "gapbottom 10");
        add(tabbedPane, "growx, h 168!");
        add(infoLb, "growx, gaptop 4, hmin 18");
        add(loginBtn, "growx, h 42!, gaptop 4");

        com.github.manolo8.darkbot.config.Config.BotSettings.Other other =
                com.github.manolo8.darkbot.config.ConfigEntity.INSTANCE.getConfig().BOT_SETTINGS.OTHER;
        JCheckBox auto = new JCheckBox("Log in automatically as the last used account", other.AUTO_LOGIN);
        auto.addActionListener(e -> other.AUTO_LOGIN = auto.isSelected());
        add(auto, "gaptop 2");

        if (other.AUTO_LOGIN && lastUser != null && saved.isLoaded()) startCountdown(lastUser);
    }

    private Timer countdown;

    /** Logs in by itself after a few seconds; any click, key press or tab change cancels it. */
    private void startCountdown(String user) {
        int[] left = {3};
        setInfoText(new Message(false, "Logging in as " + user + " in " + left[0] + "...  (click anywhere to cancel)", null));
        countdown = new Timer(1000, e -> {
            if (--left[0] <= 0) {
                cancelCountdown();
                startLogin();
            } else {
                setInfoText(new Message(false, "Logging in as " + user + " in " + left[0] + "...  (click anywhere to cancel)", null));
            }
        });
        countdown.start();

        java.awt.event.MouseAdapter cancelOnClick = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.getSource() != loginBtn) cancelCountdownWithMessage();
            }
        };
        addMouseListener(cancelOnClick);
        tabbedPane.addMouseListener(cancelOnClick);
        tabbedPane.addChangeListener(e -> cancelCountdownWithMessage());
        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(ev -> {
            if (countdown != null && countdown.isRunning()) cancelCountdownWithMessage();
        }, java.awt.AWTEvent.KEY_EVENT_MASK);
    }

    private void cancelCountdown() {
        if (countdown != null) countdown.stop();
    }

    private void cancelCountdownWithMessage() {
        if (countdown != null && countdown.isRunning()) {
            countdown.stop();
            setInfoText(new Message(false, "", null));
        }
    }

    public JButton getLoginBtn() {
        return loginBtn;
    }

    public LoginData getResult() {
        return loginData;
    }

    public void setInfoText(Message val) {
        infoLb.setText(val.text);
        infoLb.setToolTipText(val.description);
        UIUtils.setRed(infoLb, val.error);
    }

    public static class Message {
        private final boolean error;
        private final String text;
        private final String description;

        public Message(boolean error, String text, String description) {
            this.error = error;
            this.text = text;
            this.description = description;
        }
    }

    private boolean canLogIn = true;
    protected synchronized void startLogin() {
        if (!canLogIn) return;
        loginBtn.setEnabled(canLogIn = false);
        setInfoText(new Message(false, I18n.get("gui.login.info.logging_in"), null));
        new LoginTask().execute();
    }

    protected synchronized void endLogin() {
        loginBtn.setEnabled(canLogIn = true);
    }

    private class LoginTask extends SwingWorker<LoginData, Message> {
        private boolean failed = false;

        @Override
        protected void process(List<Message> chunks) {
            setInfoText(chunks.get(chunks.size() - 1));
        }

        @Override
        protected void done() {
            if (!failed) SwingUtilities.getWindowAncestor(LoginForm.this).setVisible(false);
            endLogin();
        }

        @Override
        protected LoginData doInBackground() {
            try {
                loginData.reset();

                publish(new Message(false, I18n.get("gui.login.info.logging_in"), null));
                Message msg = ((LoginScreen) tabbedPane.getSelectedComponent()).tryLogin(loginData, this::publish);
                if (msg != null) {
                    publish(msg);
                    failed = true;
                    return null;
                }

                if (loginData.isNotInitialized()) {
                    publish(new Message(false, I18n.get("gui.login.info.loading_spacemap"), null));
                    LoginUtils.findPreloader(loginData);
                }
                return loginData;
            } catch (SSLHandshakeException e) {
                publish(new Message(true, I18n.get("gui.login.error.unsupported_java"),
                        IssueHandler.createDescription(e)));
            } catch (LoginUtils.LoginException e) {
                publish(new Message(true, e.getTitle(), IssueHandler.createDescription(e)));
            } catch (Exception e) {
                publish(new Message(true, I18n.get("gui.login.error.failed_login"),
                        IssueHandler.createDescription(e)));
            }
            failed = true;
            return null;
        }
    }

}
