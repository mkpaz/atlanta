# AtlantaFX

JavaFX CSS theme collection plus additional controls. It's inspired by [FlatLaf](https://github.com/JFormDesigner/FlatLaf) and the variety of Web component frameworks.

---

![primer_light](./.screenshots/primer_light.png)
![primer_dark](./.screenshots/primer_dark.png)

## Installing

**Requirements:** JavaFX 17+ (because of `data-url` support).

TODO (after publishing on Maven Central)

## Introduction

TODO

## Motivation

**Goals**:

- SASS

  JavaFX standard themes, namely Modena and Caspian, maintained as a huge single CSS file, which is an overwhelmingly hard task. This alone makes creating a new JavaFX theme from scratch hardly possible. Also JavaFX styling is based on CSS v2.1 specification which does not provide nor variables, nor mixins, nor modules nor any other goodies that are default for modern frontend development. AtlantaFX themes are written on SASS with each component in a separate module and use recent [Dart SASS](https://sass-lang.com/dart-sass) implementation for CSS compilation. It also follows [Github Primer](https://primer.style/design/foundations/color) color system to make creating new themes more simple.

- Additional controls

  JavaFX 2.0 was started in 2011, and it introduced no additional controls since then. Some JavaFX controls are obsolete, some can be found in popular third-party libraries like [ControlsFX](https://github.com/controlsfx/controlsfx). The problem with the latter is that it provides much more than some missing controls. It provides many things that can be called a widget. That's why AtlantaFX borrows some existing controls from ControlsFX instead of supporting it directly. The rule of the thumb is to not re-invent any existing control from `javafx-controls` and to avoid widgets and everything that requires i18n support.

- Sampler application

  Theme development is not possible without some kind of demo application where you can test each control under every angle. That's what the Sampler application is. It supports hot reload, thanks to [cssfx](https://github.com/McFoggy/cssfx), and you can observe the scene graph via [Scenic View](https://github.com/JonathanGiles/scenic-view).

- Distribution and flexibility

  AtlantaFX is also distributed as a collection of CSS files. So, if you don't need additional controls, you can just download only CSS and use it via `Application.setUserAgentStylesheet()` method. If your application is only need a subset of controls, you can compile your own theme by just removing unnecessary components from SASS.

**Non-goals**:

- Replacing `javafx-controls` or standard JavaFX themes

  It's not a goal to re-invent any existing `javafx-controls` component or replace standard JavaFX themes. Libraries come and gone, but committing into the core project benefits all the community.

- Mobile support

  This is a tremendous amount of work. Just use [Gluon Mobile](https://gluonhq.com/products/mobile/).

- Providing theme API

  AtlantaFX provides the Theme interface, which is nothing but a simple wrapper around the stylesheet path. [PR](https://github.com/openjdk/jfx/pull/511) is on its way, let's hope it will ever be merged.
