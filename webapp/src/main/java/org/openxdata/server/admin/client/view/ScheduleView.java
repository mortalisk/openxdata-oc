package org.openxdata.server.admin.client.view;

import org.openxdata.server.admin.client.util.CronEntity;
import org.openxdata.server.admin.client.util.CronExpressionParser;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.model.TaskDef;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.presenter.IPresenter;
import org.openxdata.server.admin.client.presenter.WidgetDisplay;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;

/**
 * This widget displays schedules of the selected task and lets you edit them.
 * 
 * @author daniel
 * @author Mark - For the Handling of switching of Cron algorithms back to UI
 * @author Ronald.K
 * 
 */
public class ScheduleView extends Composite implements IPresenter<ScheduleView>, WidgetDisplay {

    VerticalPanel panel = new VerticalPanel();
    private CheckBox chkRunOnStartup = new CheckBox();
    RadioButton rdSeconds = new RadioButton(constants.label_runevery(),
            constants.label_second());
    RadioButton rdMinutes = new RadioButton(constants.label_runevery(),
            constants.label_minute());
    RadioButton rdHours = new RadioButton(constants.label_runevery(),
            constants.label_hour());
    RadioButton rdDays = new RadioButton(constants.label_runevery(),
            constants.label_day());
    RadioButton rdMonths = new RadioButton(constants.label_runevery(),
            constants.label_month());
    TextBox txtSeconds = new TextBox();
    TextBox txtMinutes = new TextBox();
    TextBox txtHours = new TextBox();
    TextBox txtDays = new TextBox();
    TextBox txtMonths = new TextBox();
    TextBox txtAtSeconds = new TextBox();
    TextBox txtAtMinutes = new TextBox();
    TextBox txtAtHours = new TextBox();
    CheckBox chkJan = new CheckBox(constants.month_jan());
    CheckBox chkFeb = new CheckBox(constants.month_feb());
    CheckBox chkMar = new CheckBox(constants.month_mar());
    CheckBox chkApr = new CheckBox(constants.month_apr());
    CheckBox chkMay = new CheckBox(constants.month_may());
    CheckBox chkJun = new CheckBox(constants.month_jun());
    CheckBox chkJul = new CheckBox(constants.month_jul());
    CheckBox chkAug = new CheckBox(constants.month_aug());
    CheckBox chkSep = new CheckBox(constants.month_sep());
    CheckBox chkOct = new CheckBox(constants.month_oct());
    CheckBox chkNov = new CheckBox(constants.month_nov());
    CheckBox chkDec = new CheckBox(constants.month_dec());
    RadioButton rdMonthDay = new RadioButton(constants.label_runon(),
            constants.label_dayofmonth());
    RadioButton rdWeekDay = new RadioButton(constants.label_runon(),
            constants.label_dayofweek());
    RadioButton rdAllMonthWeekDays = new RadioButton(constants.label_runon(),
            "All Days");
    TextBox txtMonthDay = new TextBox();
    CheckBox chkSun = new CheckBox(constants.day_sun());
    CheckBox chkMon = new CheckBox(constants.day_mon());
    CheckBox chkTue = new CheckBox(constants.day_tue());
    CheckBox chkWed = new CheckBox(constants.day_wed());
    CheckBox chkThu = new CheckBox(constants.day_thu());
    CheckBox chkFri = new CheckBox(constants.day_fri());
    CheckBox chkSat = new CheckBox(constants.day_sat());
    DisclosurePanel advanced;
    TextBox txtCronExpression = new TextBox();
    TextBox txtAutoCronExpression = new TextBox();
    TaskDef taskDef;
    private final EventBus eventBus;

    @Inject
    ScheduleView(EventBus eventBus) {

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(new Label(constants.label_run_once_on_startup()));
        horizontalPanel.add(chkRunOnStartup);
        chkRunOnStartup.setValue(true);
        horizontalPanel.setStyleName("openxdata-noborder");
        horizontalPanel.setSpacing(5);
        panel.add(horizontalPanel);

        horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(new Label(constants.label_runevery()));
        horizontalPanel.add(rdSeconds);
        horizontalPanel.add(txtSeconds);
        horizontalPanel.add(rdMinutes);
        horizontalPanel.add(txtMinutes);
        horizontalPanel.add(rdHours);
        horizontalPanel.add(txtHours);
        horizontalPanel.add(rdDays);
        horizontalPanel.add(txtDays);
        horizontalPanel.add(rdMonths);
        horizontalPanel.add(txtMonths);
        horizontalPanel.setStyleName("openxdata-noborder");
        horizontalPanel.setSpacing(5);

        panel.add(horizontalPanel);

        horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(new Label(constants.label_at_time()));
        horizontalPanel.add(txtAtSeconds);
        horizontalPanel.add(new Label(constants.label_second()));
        horizontalPanel.add(txtAtMinutes);
        horizontalPanel.add(new Label(constants.label_minute()));
        horizontalPanel.add(txtAtHours);
        horizontalPanel.add(new Label(constants.label_hour()));
        horizontalPanel.setStyleName("openxdata-noborder");
        horizontalPanel.setSpacing(5);
        panel.add(horizontalPanel);

        horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(new Label(constants.label_month()));
        horizontalPanel.add(chkJan);
        horizontalPanel.add(chkFeb);
        horizontalPanel.add(chkMar);
        horizontalPanel.add(chkApr);
        horizontalPanel.add(chkMay);
        horizontalPanel.add(chkJun);
        horizontalPanel.add(chkJul);
        horizontalPanel.add(chkAug);
        horizontalPanel.add(chkSep);
        horizontalPanel.add(chkOct);
        horizontalPanel.add(chkNov);
        horizontalPanel.add(chkDec);
        horizontalPanel.setStyleName("openxdata-noborder");
        horizontalPanel.setSpacing(5);
        panel.add(horizontalPanel);

        horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(new Label(constants.label_on()));
        horizontalPanel.add(rdAllMonthWeekDays);
        horizontalPanel.add(rdMonthDay);
        horizontalPanel.add(txtMonthDay);
        horizontalPanel.add(rdWeekDay);
        horizontalPanel.add(chkSun);
        horizontalPanel.add(chkMon);
        horizontalPanel.add(chkTue);
        horizontalPanel.add(chkWed);
        horizontalPanel.add(chkThu);
        horizontalPanel.add(chkFri);
        horizontalPanel.add(chkSat);
        horizontalPanel.setStyleName("openxdata-noborder");
        horizontalPanel.setSpacing(5);
        panel.add(horizontalPanel);
        rdWeekDay.setValue(true);

        horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(new Label(constants.label_custom_cron_expression()));
        horizontalPanel.add(txtCronExpression);
        txtAutoCronExpression.setEnabled(false);
        horizontalPanel.add(new Label(constants.label_automatic_cron_expression()));
        horizontalPanel.add(txtAutoCronExpression);
        horizontalPanel.setStyleName("openxdata-noborder");
        horizontalPanel.setSpacing(5);
        advanced = new DisclosurePanel(constants.label_advanced_schedule());
        advanced.setAnimationEnabled(true);
        advanced.setContent(horizontalPanel);
        panel.add(advanced);

        enableRunEvery(false);

        String width = "30px";
        txtSeconds.setWidth(width);
        txtMinutes.setWidth(width);
        txtHours.setWidth(width);
        txtDays.setWidth(width);
        txtMonths.setWidth(width);

        txtAtSeconds.setWidth(width);
        txtAtMinutes.setWidth(width);
        txtAtHours.setWidth(width);

        txtMonthDay.setWidth(width);

        setupEventListeners();
        enableNumericOnly();

        panel.setSpacing(10);

        initWidget(panel);

        panel.addStyleName("cw-FlexTable3");
        // panel.setStyleName("openxdata-noborder");

        chkRunOnStartup.setEnabled(false);
        rdAllMonthWeekDays.setValue(true);
        this.eventBus = eventBus;
        bindHandlers();
    }

    private void enableNumericOnly() {
        Utilities.allowNumericOnly(txtSeconds, false);
        Utilities.allowNumericOnly(txtMinutes, false);
        Utilities.allowNumericOnly(txtHours, false);
        Utilities.allowNumericOnly(txtDays, false);
        Utilities.allowNumericOnly(txtMonths, false);

        Utilities.allowNumericOnly(txtAtSeconds, false);
        Utilities.allowNumericOnly(txtAtMinutes, false);
        Utilities.allowNumericOnly(txtAtHours, false);

        Utilities.allowNumericOnly(txtMonthDay, false);
    }

    private void enableRunEvery(boolean enabled) {

        rdSeconds.setEnabled(enabled);
        rdMinutes.setEnabled(enabled);
        rdHours.setEnabled(enabled);
        rdDays.setEnabled(enabled);
        rdMonths.setEnabled(enabled);

        rdAllMonthWeekDays.setEnabled(enabled);
        rdMonthDay.setEnabled(enabled);
        rdWeekDay.setEnabled(enabled);

        enableRunEveryValue(enabled);

        // txtAtMinutes.setEnabled(enabled);
        // txtAtHours.setEnabled(enabled);
        // txtAtSeconds.setEnabled(enabled);
        updateTimeAtWidgets();

        txtMonthDay.setEnabled(enabled && rdMonthDay.getValue());

        enableMonths(enabled);
        enableWeekDays(enabled && rdWeekDay.getValue());

        txtCronExpression.setEnabled(enabled);
    }

    private void enabledMonthDay(boolean enabled) {
        /*
         * if(!chkRunOnStartup.getValue()){ String s = txtDays.getText();
         * if(s.trim().length() == 0) enabled = true; else{
         * if(Integer.parseInt(s) < 2) enabled = true; } }
         */

        txtMonthDay.setEnabled(enabled && rdMonthDay.getValue());
        rdMonthDay.setEnabled(enabled);
    }

    private void enableMonths(boolean enabled) {
        if (!chkRunOnStartup.getValue()) {
            String s = txtMonths.getText();

            if (s.trim().length() == 0 || Integer.parseInt(s) < 2)
                enabled = true;
            else
                enabled = false;
        }

        enableMonths0(enabled);
    }

    private void enableMonths0(boolean enabled) {
        chkJan.setEnabled(enabled);
        chkFeb.setEnabled(enabled);
        chkMar.setEnabled(enabled);
        chkApr.setEnabled(enabled);
        chkMay.setEnabled(enabled);
        chkJun.setEnabled(enabled);
        chkJul.setEnabled(enabled);
        chkAug.setEnabled(enabled);
        chkSep.setEnabled(enabled);
        chkOct.setEnabled(enabled);
        chkNov.setEnabled(enabled);
        chkDec.setEnabled(enabled);
    }

    private void setupEventListeners() {
        rdSeconds.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                enableRunEveryValue(rdSeconds.getValue());

                txtAtSeconds.setEnabled(false);
                txtAtMinutes.setEnabled(false);
                txtAtHours.setEnabled(false);
                enableMonths(!rdMonths.getValue());
                enabledMonthDay(!rdDays.getValue());
                enableWeekDays2();

                txtSeconds.setFocus(true);
                updateAutoCronExpression();
            }
        });
        rdMinutes.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                boolean enabled = rdMinutes.getValue();
                enableRunEveryValue(enabled);

                txtAtSeconds.setEnabled(enabled);
                txtAtMinutes.setEnabled(false);
                txtAtHours.setEnabled(false);
                enableMonths(!rdMonths.getValue());
                enabledMonthDay(!rdDays.getValue());
                enableWeekDays2();

                txtMinutes.setFocus(true);
                updateAutoCronExpression();
            }
        });
        rdHours.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                boolean enabled = rdHours.getValue();
                enableRunEveryValue(enabled);

                txtAtSeconds.setEnabled(enabled);
                txtAtMinutes.setEnabled(enabled);
                txtAtHours.setEnabled(false);
                enableMonths(!rdMonths.getValue());
                enabledMonthDay(!rdDays.getValue());
                enableWeekDays2();

                txtHours.setFocus(true);
                updateAutoCronExpression();
            }
        });
        rdDays.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                boolean enabled = rdDays.getValue();
                enableRunEveryValue(enabled);

                txtAtSeconds.setEnabled(enabled);
                txtAtMinutes.setEnabled(enabled);
                txtAtHours.setEnabled(enabled);
                enableMonths(!rdMonths.getValue());
                enabledMonthDay(!rdDays.getValue());
                txtDays.setFocus(true);
                updateAutoCronExpression();

                enabled = false;
                String s = txtDays.getText();
                if (s.trim().length() == 0 || Integer.parseInt(s) < 2)
                    enabled = true;
                rdWeekDay.setEnabled(enabled);
                enableWeekDays(enabled);
            }
        });
        rdMonths.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                boolean enabled = rdMonths.getValue();
                enableRunEveryValue(enabled);

                txtAtSeconds.setEnabled(enabled);
                txtAtMinutes.setEnabled(enabled);
                txtAtHours.setEnabled(enabled);

                enableMonths(!enabled);
                enabledMonthDay(!rdDays.getValue());
                enableWeekDays2();

                txtMonths.setFocus(true);
                updateAutoCronExpression();
            }
        });
        chkRunOnStartup.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                enableRunEvery(!chkRunOnStartup.getValue());

                if (chkRunOnStartup.getValue()) {
                    clearControls();
                    taskDef.setCronExpression(null);
                } else
                    rdAllMonthWeekDays.setValue(true);

                if (taskDef != null) {
                    taskDef.setStartOnStartup(chkRunOnStartup.getValue());
                    taskDef.setDirty(true);
                }
            }
        });

        rdMonthDay.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                txtMonthDay.setEnabled(rdMonthDay.getValue());
                enableWeekDays(rdWeekDay.getValue());

                txtMonthDay.setFocus(true);
                updateAutoCronExpression();
            }
        });
        rdWeekDay.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                enableWeekDays(rdWeekDay.getValue());
                txtMonthDay.setEnabled(rdMonthDay.getValue());
                updateAutoCronExpression();
            }
        });
        rdAllMonthWeekDays.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });

        setupMonthSelectionEventListeners();
        setupWeekDaySelectionEventListeners();
        setupTextChangeEventListeners();
    }

    private void enableWeekDays2() {
        boolean anabled = false;
        if (!rdDays.getValue() && rdWeekDay.getValue())
            anabled = true;
        rdWeekDay.setEnabled(true);
        enableWeekDays(anabled);
    }

    private void setupTextChangeEventListeners() {
        txtSeconds.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent arg0) {
                updateAutoCronExpression();
            }
        });
        txtMinutes.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent arg0) {
                updateAutoCronExpression();
            }
        });
        txtHours.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent arg0) {
                updateAutoCronExpression();
            }
        });
        txtDays.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent arg0) {
                updateAutoCronExpression();
                enabledMonthDay(false);

                boolean enabled = false;
                String s = txtDays.getText();
                if (s.trim().length() == 0 || Integer.parseInt(s) < 2)
                    enabled = true;
                enableWeekDays(enabled);
                rdWeekDay.setEnabled(enabled);
            }
        });
        txtMonths.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent arg0) {
                updateAutoCronExpression();
                enableMonths(false);
            }
        });

        txtAtSeconds.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent arg0) {
                updateAutoCronExpression();
            }
        });
        txtAtMinutes.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent arg0) {
                updateAutoCronExpression();
            }
        });
        txtAtHours.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent arg0) {
                updateAutoCronExpression();
            }
        });

        txtMonthDay.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent arg0) {
                updateAutoCronExpression();
            }
        });
    }

    private void setupWeekDaySelectionEventListeners() {
        chkSun.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkMon.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkTue.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkWed.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkThu.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkFri.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkSat.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
    }

    private void setupMonthSelectionEventListeners() {
        chkJan.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkFeb.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkMar.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkApr.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkMay.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkJun.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkJul.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkAug.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkSep.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkOct.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkNov.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
        chkDec.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAutoCronExpression();
            }
        });
    }

    private void enableRunEveryValue(boolean enabled) {
        txtSeconds.setEnabled(enabled && rdSeconds.getValue());
        txtMinutes.setEnabled(enabled && rdMinutes.getValue());
        txtHours.setEnabled(enabled && rdHours.getValue());
        txtDays.setEnabled(enabled && rdDays.getValue());
        txtMonths.setEnabled(enabled && rdMonths.getValue());
    }

    private void enableWeekDays(boolean enabled) {
        if (!rdWeekDay.getValue())
            enabled = false;

        chkSun.setEnabled(enabled);
        chkMon.setEnabled(enabled);
        chkTue.setEnabled(enabled);
        chkWed.setEnabled(enabled);
        chkThu.setEnabled(enabled);
        chkFri.setEnabled(enabled);
        chkSat.setEnabled(enabled);
    }

    private void updateAutoCronExpression() {
        txtAutoCronExpression.setText(getCronExpression());

        if (taskDef != null) {
            taskDef.setStartOnStartup(chkRunOnStartup.getValue());

            String cronExpression = txtAutoCronExpression.getText();
            if (txtCronExpression.getText().trim().length() > 0)
                cronExpression = "CUSTOM=" + txtCronExpression.getText();
            taskDef.setCronExpression(cronExpression);
            taskDef.setDirty(true);
        }
    }

    public String getCronExpression() {
        String expression = txtCronExpression.getText();
        if (expression != null && expression.trim().length() > 0)
            return expression;

        if (rdSeconds.getValue()) {
            expression = "0/" + getEverySeconds() + " * *";
            expression += " " + getMonthDayMonthAndWeekDay();
        } else if (rdMinutes.getValue()) {
            expression = getAtSeconds();
            expression += " 0/" + getEveryMinutes() + " *";
            expression += " " + getMonthDayMonthAndWeekDay();

        } else if (rdHours.getValue()) {
            expression = getAtSeconds();
            expression += " " + getAtMinutes();
            expression += " 0/" + getEveryHours();
            expression += " " + getMonthDayMonthAndWeekDay();
        } else if (rdDays.getValue()) {
            expression = getAtSeconds();
            expression += " " + getAtMinutes();
            expression += " " + getAtHours();
            expression += getEveryDays();
            expression += " " + getMonthAndWeekDay();
        } else if (rdMonths.getValue()) {
            expression = getAtSeconds();
            expression += " " + getAtMinutes();
            expression += " " + getAtHours();
            expression += " " + getMonthDay();
            expression += getEveryMonths();
            expression += " " + getWeekDay();
        }
        return expression;
    }

    private void updateMonths(String months) {

        int switchMonth = Integer.parseInt(months);

        switch (switchMonth) {
            case 1:
                chkJan.setValue(true);
                break;
            case 2:
                chkFeb.setValue(true);
                break;
            case 3:
                chkMar.setValue(true);
                break;
            case 4:
                chkApr.setValue(true);
                break;
            case 5:
                chkMay.setValue(true);
                break;
            case 6:
                chkJun.setValue(true);
                break;
            case 7:
                chkJul.setValue(true);
                break;
            case 8:
                chkAug.setValue(true);
                break;
            case 9:
                chkSep.setValue(true);
                break;
            case 10:
                chkOct.setValue(true);
                break;
            case 11:
                chkNov.setValue(true);
                break;
            case 12:
                chkDec.setValue(true);
                break;
        }

    }

    private String getEverySeconds() {
        String s = txtSeconds.getText();
        if (s.trim().length() == 0)
            s = "1";
        return s;
    }

    private String getEveryMinutes() {
        String s = txtMinutes.getText();
        if (s.trim().length() == 0)
            s = "1";
        return s;
    }

    private String getEveryHours() {
        String s = txtHours.getText();
        if (s.trim().length() == 0)
            s = "1";
        return s;
    }

    private String getEveryDays() {
        String s = txtDays.getText();
        if ((s.trim().length() == 0 || Integer.parseInt(s) < 2)) {
            if (anyWeekDaySelected())
                s = " ?";// + getWeekDay();
            else
                s = " 1/1";
        } else
            s = " 1/" + s;

        return s;
    }

    private String getEveryMonths() {
        String s = txtMonths.getText();
        if (s.trim().length() == 0 || Integer.parseInt(s) < 2)
            if (anyMonthSelected())
                s = " " + getMonth();
            else
                s = " 1/1";
        else
            s = " 1/" + s;

        return s;
    }

    public boolean getRunOnStartup() {
        return chkRunOnStartup.getValue();
    }

    private String getAtSeconds() {
        String atSeconds = txtAtSeconds.getText();
        if (atSeconds.length() == 0)
            atSeconds = "*";
        // else
        // atSeconds = String.valueOf(Integer.parseInt(atSeconds));
        return atSeconds;

    }

    private String getMonthDayMonthAndWeekDay() {
        String expression = getMonthDay();
        expression += " " + getMonthAndWeekDay();

        return expression;
    }

    private String getMonthDay() {
        String expression = null;

        if (rdMonthDay.getValue()) {
            expression = txtMonthDay.getText();
            if (expression.trim().length() == 0)
                expression = "*";
        } else
            expression = rdWeekDay.getValue() ? "?" : "*";

        return expression;
    }

    private String getMonthAndWeekDay() {
        String expression = getMonth();
        expression += " " + getWeekDay();

        return expression;
    }

    private String getAtMinutes() {
        String atMinutes = txtAtMinutes.getText();
        if (atMinutes.length() == 0)
            atMinutes = "*";
        // else
        // atMinutes = String.valueOf(Integer.parseInt(atMinutes)-1);
        return atMinutes;

    }

    private String getAtHours() {
        String atHours = txtAtHours.getText();
        if (atHours.length() == 0)
            atHours = "*";
        // else
        // atHours = String.valueOf(Integer.parseInt(atHours)-1);
        return atHours;

    }

    private String getMonth() {
        String month = "";

        if (chkJan.getValue())
            month = "1";

        if (chkFeb.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "2";
        }

        if (chkMar.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "3";
        }

        if (chkApr.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "4";
        }

        if (chkMay.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "5";
        }

        if (chkJun.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "6";
        }

        if (chkJul.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "7";
        }

        if (chkAug.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "8";
        }

        if (chkSep.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "9";
        }

        if (chkOct.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "10";
        }

        if (chkNov.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "11";
        }

        if (chkDec.getValue()) {
            if (month.length() > 0)
                month += ",";
            month += "12";
        }

        if (month.length() == 0)
            month = "*";

        return month;
    }

    private String getWeekDay() {
        if (!rdWeekDay.getValue())
            return "?";

        if (rdDays.getValue()) {
            String s = txtDays.getText();
            if (s.trim().length() > 0 && Integer.parseInt(s) > 1)
                return "?";
            if (!this.anyWeekDaySelected())
                return "?";
        }

        String weekDay = "";

        if (chkSun.getValue())
            weekDay = "1";

        if (chkMon.getValue()) {
            if (weekDay.length() > 0)
                weekDay += ",";
            weekDay += "2";
        }

        if (chkTue.getValue()) {
            if (weekDay.length() > 0)
                weekDay += ",";
            weekDay += "3";
        }

        if (chkWed.getValue()) {
            if (weekDay.length() > 0)
                weekDay += ",";
            weekDay += "4";
        }

        if (chkThu.getValue()) {
            if (weekDay.length() > 0)
                weekDay += ",";
            weekDay += "5";
        }

        if (chkFri.getValue()) {
            if (weekDay.length() > 0)
                weekDay += ",";
            weekDay += "6";
        }

        if (chkSat.getValue()) {
            if (weekDay.length() > 0)
                weekDay += ",";
            weekDay += "7";
        }

        if (weekDay.length() == 0)
            weekDay = "*";

        return weekDay;
    }

    private boolean anyWeekDaySelected() {
        return ((chkSun.getValue() || chkMon.getValue() || chkTue.getValue()
                || chkWed.getValue() || chkThu.getValue() || chkFri.getValue() || chkSat.getValue()) && rdWeekDay.getValue());
    }

    private boolean anyMonthSelected() {
        return (chkJan.getValue() || chkFeb.getValue() || chkMar.getValue()
                || chkApr.getValue() || chkMay.getValue() || chkJun.getValue()
                || chkJul.getValue() || chkAug.getValue() || chkSep.getValue()
                || chkOct.getValue() || chkNov.getValue() || chkDec.getValue());
    }

    public void onItemSelected(TaskDef taskDef) {
        this.taskDef = taskDef;

        chkRunOnStartup.setEnabled(taskDef != null);

        clearControls();

        if (taskDef != null) {
            chkRunOnStartup.setValue(taskDef.isStartOnStartup());
            String cronExpression = taskDef.getCronExpression();

            // updateCronExpressionUI(taskDef);
            if (cronExpression != null && cronExpression.length() > 0) {
                if (cronExpression.startsWith("CUSTOM="))
                    this.txtCronExpression.setText(cronExpression.substring(7,
                            cronExpression.length()));
                else {
                    this.txtAutoCronExpression.setText(cronExpression);
                    parseCronAndUpdateUI(cronExpression);
                }
            }
        }

        enableRunEvery(!chkRunOnStartup.getValue() && taskDef != null);
    }

    /**
     * Parses the cron expression and updates the wigdets appropriately
     *
     * @param expression
     *            cron expression
     */
    private void parseCronAndUpdateUI(String expression) {

        // Parse Cron expression
        CronExpressionParser parser = new CronExpressionParser(
                expression.replace('?', '*'));

        // Update the Seconds widgets by passing the txtAtSeconds, txtSeconds,
        // rdSeconds widgets
        updateTimeWidgets(parser.getSecond(), txtAtSeconds, txtSeconds,
                rdSeconds);
        // Update the Minutes widgets by passing thetxtAtMinutes, txtMinutes,
        // rdMinutes widgets
        updateTimeWidgets(parser.getMinute(), txtAtMinutes, txtMinutes,
                rdMinutes);
        // Update the hours widgets txtAtHours, txtHours, rdHours wigdets
        updateTimeWidgets(parser.getHour(), txtAtHours, txtHours, rdHours);

        updateMonthsWidgets(parser.getMonth());
        updateDaysWidgets(parser.getDayOfWeek());
        updateDayOfMonthWidgets(parser.getDayOfMonth());

        updateTimeAtWidgets();
    }

    /**
     * Sets the times specified TextBoxes depending on the cron entity
     *
     * @param entity
     *            The CronEntity
     * @param atTime
     *            TextBox to alter text having atTime information
     * @param runevery
     *            TextBox to alter text for runevery
     * @param btnRadio
     *            RadioButton to setValue if runevery is set
     */
    private void updateTimeWidgets(CronEntity entity, TextBox atTime,
            TextBox runevery, RadioButton btnRadio) {
        if (entity.getValues() == null)// make sure entity has values
            return;

        boolean repeating = entity.getRepeat() != -1;// check for runevery
        if (repeating) {
            btnRadio.setValue(true);
            runevery.setText(entity.getRepeat() + "");
            enableRunEveryValue(false);
        } else {
            atTime.setText(entity.getValues()[0] + "");
        }
    }

    /**
     * Update the runevry month or checks all relative CheckBoxes for the month
     *
     * @param entity
     *            contain selected values
     */
    private void updateMonthsWidgets(CronEntity entity) {
        if (entity.getValues() == null)
            return;

        boolean repeating = entity.getRepeat() != -1;// check for runevery
        if (repeating) {
            rdMonths.setValue(true);
            txtMonths.setText(entity.getRepeat() + "");
            enableMonths0(false);

        } else {
            int[] months = entity.getValues();// get the values
            for (int i = 0; i < months.length; i++)
                updateMonths("" + months[i]);
            enabledMonthDay(false);
        }
    }

    private void updateDaysWidgets(CronEntity entity) {
        if (entity.getValues() == null) {
            rdAllMonthWeekDays.setValue(true);
            return;
        }

        rdAllMonthWeekDays.setEnabled(false);
        boolean repeating = entity.getRepeat() != -1;// check for runevery
        if (repeating) {
            rdDays.setValue(true);
            enableWeekDays(false);
            txtDays.setText(entity.getRepeat() + "");
        } else {
            int[] days = entity.getValues();
            for (int i = 0; i < days.length; i++)
                enableDay(days[i]);
            rdWeekDay.setValue(true);
            enableWeekDays2();
        }
    }

    private void updateDayOfMonthWidgets(CronEntity entity) {
        if (entity.getValues() == null)
            return;
        boolean repeating = entity.getRepeat() != -1;// check for runevery
        if (repeating) {
            rdDays.setValue(true);
            enableWeekDays(false);
            txtDays.setText(entity.getRepeat() + "");
        } else {
            txtMonthDay.setText(entity.getValues()[0] + "");
            enableWeekDays(false);
            rdMonthDay.setValue(true);
        }
    }

    private void enableDay(int i) {
        CheckBox toCheck = null;
        switch (i) {
            case 1:
                toCheck = chkMon;
                break;
            case 2:
                toCheck = chkTue;
                break;
            case 3:
                toCheck = chkWed;
                break;
            case 4:
                toCheck = chkThu;
                break;
            case 5:
                toCheck = chkFri;
                break;

            case 6:
                toCheck = chkSat;
                break;
            case 7:
                toCheck = chkSun;
                break;
        }
        if (toCheck != null)
            toCheck.setValue(true);

    }

    private void updateTimeAtWidgets() {

        txtAtSeconds.setEnabled(!rdSeconds.getValue()
                && !chkRunOnStartup.getValue());

        txtAtMinutes.setEnabled(!rdMinutes.getValue() && !rdSeconds.getValue()
                && !chkRunOnStartup.getValue());

        txtAtHours.setEnabled(!rdMinutes.getValue() && !rdSeconds.getValue()
                && !rdHours.getValue() && !chkRunOnStartup.getValue());

    }

    private void clearControls() {
        chkJan.setValue(false);
        chkFeb.setValue(false);
        chkMar.setValue(false);
        chkApr.setValue(false);
        chkMay.setValue(false);
        chkJun.setValue(false);
        chkJul.setValue(false);
        chkAug.setValue(false);
        chkSep.setValue(false);
        chkOct.setValue(false);
        chkNov.setValue(false);
        chkDec.setValue(false);

        chkSun.setValue(false);
        chkMon.setValue(false);
        chkTue.setValue(false);
        chkWed.setValue(false);
        chkThu.setValue(false);
        chkFri.setValue(false);
        chkSat.setValue(false);

        rdSeconds.setValue(false);
        rdMinutes.setValue(false);
        rdHours.setValue(false);
        rdDays.setValue(false);
        rdMonths.setValue(false);
        rdWeekDay.setValue(false);
        rdMonthDay.setValue(false);
        rdAllMonthWeekDays.setValue(false);

        txtSeconds.setText(null);
        txtMinutes.setText(null);
        txtHours.setText(null);
        txtDays.setText(null);
        txtMonths.setText(null);
        txtAtSeconds.setText(null);
        txtAtHours.setText(null);
        txtAtMinutes.setText(null);
        txtMonthDay.setText(null);
        txtCronExpression.setText(null);
        txtAutoCronExpression.setText(null);
    }

    @Override
    public ScheduleView getDisplay() {
        return this;
    }

    private void bindHandlers() {
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<TaskDef>() {

            @Override
            public void onSelected(Composite sender, TaskDef item) {
                onItemSelected(item);
            }
        }).forClass(TaskDef.class);
    }
}
