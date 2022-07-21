/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2013, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package atlantafx.base.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * The DatePicker control allows the user to select a date. The calendar is based on either
 * the standard ISO-8601 chronology or any of the other chronology classes defined in the
 * java.time.chrono package.
 * <p>
 * The {@link #valueProperty() value} property represents the currently selected
 * {@link LocalDate}. The default value is null.
 * <p>
 * The {@link #chronologyProperty() chronology} property specifies a calendar system to be used
 * for parsing, displaying, and choosing dates.
 * <p>
 * The {@link #valueProperty() value} property is always defined in the ISO calendar system,
 * however, so applications based on a different chronology may use the conversion methods
 * provided in the {@link java.time.chrono.Chronology} API to get or set the corresponding
 * {@link java.time.chrono.ChronoLocalDate} value.
 */
public class InlineDatePicker extends Control {

    protected LocalDate lastValidDate = null;
    protected Chronology lastValidChronology = IsoChronology.INSTANCE;

    /** Creates a default DatePicker instance with a <code>null</code> date value set. */
    public InlineDatePicker() {
        this(null);

        valueProperty().addListener(obs -> {
            LocalDate date = getValue();
            Chronology chrono = getChronology();

            if (isValidDate(chrono, date)) {
                lastValidDate = date;
            } else {
                System.err.println("Restoring value to " + (lastValidDate == null ? "null" : lastValidDate));
                setValue(lastValidDate);
            }
        });

        chronologyProperty().addListener(observable -> {
            LocalDate date = getValue();
            Chronology chrono = getChronology();

            if (isValidDate(chrono, date)) {
                lastValidChronology = chrono;
            } else {
                System.err.println("Restoring value to " + lastValidChronology);
                setChronology(lastValidChronology);
            }
        });
    }

    /**
     * Creates a DatePicker instance and sets the {@link #valueProperty() value} to the given date.
     *
     * @param localDate to be set as the currently selected date in the DatePicker. Can be null.
     */
    public InlineDatePicker(LocalDate localDate) {
        setValue(localDate);
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /** {@inheritDoc} */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new InlineDatePickerSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private final ObjectProperty<LocalDate> value = new SimpleObjectProperty<>(this, "value");

    public final LocalDate getValue() {
        return valueProperty().get();
    }

    public final void setValue(LocalDate value) {
        valueProperty().set(value);
    }

    public ObjectProperty<LocalDate> valueProperty() { return value; }

    /**
     * A custom cell factory can be provided to customize individual day cells
     * Refer to {@link DateCell} and {@link Cell} for more information on cell factories.
     */
    private ObjectProperty<Callback<InlineDatePicker, DateCell>> dayCellFactory;

    public final void setDayCellFactory(Callback<InlineDatePicker, DateCell> value) {
        dayCellFactoryProperty().set(value);
    }

    public final Callback<InlineDatePicker, DateCell> getDayCellFactory() {
        return (dayCellFactory != null) ? dayCellFactory.get() : null;
    }

    public final ObjectProperty<Callback<InlineDatePicker, DateCell>> dayCellFactoryProperty() {
        if (dayCellFactory == null) {
            dayCellFactory = new SimpleObjectProperty<>(this, "dayCellFactory");
        }
        return dayCellFactory;
    }

    /**
     * The calendar system used for parsing, displaying, and choosing dates in the DatePicker
     * control.
     * <p>
     * The default is usually {@link IsoChronology} unless provided explicitly
     * in the {@link Locale} by use of a Locale calendar extension.
     * <p>
     * Setting the value to <code>null</code> will restore the default chronology.
     *
     * @return a property representing the Chronology being used
     */
    public ObjectProperty<Chronology> chronologyProperty() {
        return chronology;
    }

    private final ObjectProperty<Chronology> chronology = new SimpleObjectProperty<>(this, "chronology", null);

    public final Chronology getChronology() {
        Chronology chrono = chronology.get();
        if (chrono == null) {
            try {
                chrono = Chronology.ofLocale(Locale.getDefault(Locale.Category.FORMAT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (chrono == null) {
                chrono = IsoChronology.INSTANCE;
            }
        }
        return chrono;
    }

    public final void setChronology(Chronology value) {
        chronology.setValue(value);
    }

    /**
     * Whether the DatePicker popup should display a column showing week numbers.
     * <p>
     * The default value is specified in a resource bundle, and depends on the country of the
     * current locale.
     *
     * @return true if popup should display a column showing week numbers
     */
    public final BooleanProperty showWeekNumbersProperty() {
        if (showWeekNumbers == null) {
            showWeekNumbers = new StyleableBooleanProperty(false) {
                @Override
                public CssMetaData<InlineDatePicker, Boolean> getCssMetaData() {
                    return StyleableProperties.SHOW_WEEK_NUMBERS;
                }

                @Override
                public Object getBean() {
                    return InlineDatePicker.this;
                }

                @Override
                public String getName() {
                    return "showWeekNumbers";
                }
            };
        }
        return showWeekNumbers;
    }

    private BooleanProperty showWeekNumbers;

    public final void setShowWeekNumbers(boolean value) {
        showWeekNumbersProperty().setValue(value);
    }

    public final boolean isShowWeekNumbers() {
        return showWeekNumbersProperty().getValue();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Stylesheet Handling                                                   //
    ///////////////////////////////////////////////////////////////////////////

    private static final String DEFAULT_STYLE_CLASS = "inline-date-picker";

    private static class StyleableProperties {

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        private static final CssMetaData<InlineDatePicker, Boolean> SHOW_WEEK_NUMBERS =
                new CssMetaData<>("-fx-show-week-numbers", BooleanConverter.getInstance(), false) {
                    @Override
                    public boolean isSettable(InlineDatePicker n) {
                        return n.showWeekNumbers == null || !n.showWeekNumbers.isBound();
                    }

                    @Override
                    @SuppressWarnings("RedundantCast")
                    public StyleableProperty<Boolean> getStyleableProperty(InlineDatePicker n) {
                        return (StyleableProperty<Boolean>) (WritableValue<Boolean>) n.showWeekNumbersProperty();
                    }
                };

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(styleables, SHOW_WEEK_NUMBERS);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its superclasses.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /** {@inheritDoc} */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    static boolean isValidDate(Chronology chrono, LocalDate date, int offset, ChronoUnit unit) {
        if (date != null) {
            try {
                return isValidDate(chrono, date.plus(offset, unit));
            } catch (DateTimeException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    static boolean isValidDate(Chronology chrono, LocalDate date) {
        try {
            if (date != null) { chrono.date(date); }
            return true;
        } catch (DateTimeException e) {
            e.printStackTrace();
            return false;
        }
    }
}
