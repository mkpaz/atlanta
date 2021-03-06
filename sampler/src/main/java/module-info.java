/* SPDX-License-Identifier: MIT */

module atlantafx.sampler {

    requires atlantafx.base;

    requires java.desktop;
    requires javafx.swing;
    requires javafx.web;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;

    requires fr.brouillard.oss.cssfx;
    requires datafaker;

    exports atlantafx.sampler;
    exports atlantafx.sampler.fake;
    exports atlantafx.sampler.fake.domain;
    exports atlantafx.sampler.layout;
    exports atlantafx.sampler.page;
    exports atlantafx.sampler.page.general;
    exports atlantafx.sampler.page.components;
    exports atlantafx.sampler.page.showcase;
    exports atlantafx.sampler.theme;
    exports atlantafx.sampler.util;

    opens atlantafx.sampler.fake.domain;

    // resources
    opens atlantafx.sampler.assets.fonts;
    opens atlantafx.sampler.assets.highlightjs;
    opens atlantafx.sampler.assets.styles;
}
