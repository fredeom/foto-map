package meraschool.controllers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

import meraschool.App;
import meraschool.models.LinkModel;
import meraschool.models.LocationModel;
import meraschool.models.ViewModel;
import meraschool.views.PreviewWindow;
import meraschool.views.ViewModelListener;

public class ViewController {

    public App app;
    public ViewModel viewModel;

    public ViewController(App app) {
        this(app, new ViewModel());
    }

    public ViewController(App app, ViewModel model) {
        this.app = app;
        this.viewModel = model;
        model.addListener(getViewModelListener());
    }

    public Point getImageSize() {
        if (viewModel.getViewId() == 0) {
            return new Point(600, 400);
        } else {
            BufferedImage bi = null;
            try {
                Path imagePath = app.dbConnector.getImagePathByViewId(viewModel.getViewId());
                if (imagePath == null) {
                    return null;
                }
                bi = ImageIO.read(imagePath.toFile());
            } catch (IOException e) {
                e.printStackTrace();
                bi = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
            }
            return new Point(bi.getWidth(), bi.getHeight());
        }
    }

    public Resource getImage() {
        if (viewModel.getViewId() == 0) {
            return new ClassResource("/1.jpg", app);
        } else {
            return new StreamResource(new MyImageSource(), String.valueOf(Math.random()) + ".jpg", app);
        }
    }

    @SuppressWarnings("serial")
    public class MyImageSource implements StreamSource {
        ByteArrayOutputStream imagebuffer = null;

        public InputStream getStream() {
            try {
                List<LinkModel> lml = app.dbConnector.getLinksByViewId(viewModel.getViewId());

                BufferedImage img = ImageIO.read(app.dbConnector.getImagePathByViewId(viewModel.getViewId()).toFile());
                Graphics2D drawable = img.createGraphics();

                Color c = new Color(0.2f, 0.5f, 1.0f, 0.3f);
                drawable.setColor(c);

                if (lml != null) {
                    for (LinkModel lm : lml) {
                        int x = (int) (lm.getX() * img.getWidth());
                        int y = (int) (lm.getY() * img.getHeight());
                        drawable.setStroke(new BasicStroke(10));
                        drawable.drawRect(x - 5, y - 5, 10, 10);
                    }
                }

                imagebuffer = new ByteArrayOutputStream();
                ImageIO.write(img, "png", imagebuffer);
                return new ByteArrayInputStream(imagebuffer.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @SuppressWarnings("serial")
    public ClickListener getImageClickListener() {
        return new ClickListener() {
            public void click(ClickEvent event) {
                int viewId = viewModel.getViewId();
                float width = ((Embedded) event.getSource()).getWidth();
                float height = ((Embedded) event.getSource()).getHeight();
                if (viewId == 0) {
                    if (getThis() instanceof PreviewController) {
                        app.getMainWindow().showNotification("Pleace select or add location");
                        return;
                    }
                    Window w = new PreviewWindow(
                            new PreviewController(app, 1, new LocationModel(null, app.dbConnector.getLocationList()),
                                    new ViewModel(0, null, viewModel)));
                    w.setModal(true);
                    app.getMainWindow().addWindow(w);
                    w.setCloseShortcut(KeyCode.ESCAPE, null);
                    w.focus();
                } else {
                    List<LinkModel> links = app.dbConnector.getLinksByViewId(viewId);
                    if (links != null) {
                        for (LinkModel lm : links) {
                            if (lm.catches(event.getRelativeX(), event.getRelativeY(), (int) width, (int) height)) {
                                if (event.getButton() == ClickEvent.BUTTON_LEFT) {
                                    viewModel.selectViewId(lm.viewRefId);
                                } else if (event.getButton() == ClickEvent.BUTTON_RIGHT) {
                                    app.dbConnector.removeLink(lm);
                                    viewModel.fireViewChanged();
                                }
                                return;
                            }
                        }
                    }
                    if (event.getButton() == ClickEvent.BUTTON_LEFT) {
                        if (viewModel.parentViewModel != null) {
                            viewModel.fireViewClosing();
                            if (viewModel.editLink == null) {
                                viewModel.parentViewModel.selectViewId(viewId);
                            } else {
                                viewModel.parentViewModel.fireViewChanged();
                            }
                        } else {
                            PreviewController c = new PreviewController(app, 1,
                                    new LocationModel(app.dbConnector.getLocationByViewId(viewId),
                                            app.dbConnector.getLocationList()),
                                    new ViewModel(viewId, null, viewModel));
                            Window w = new PreviewWindow(c);
                            w.setModal(true);
                            app.getMainWindow().addWindow(w);
                            w.setCloseShortcut(KeyCode.ESCAPE, null);
                            w.focus();
                        }
                    } else if (event.getButton() == ClickEvent.BUTTON_RIGHT) {
                        int viewLevel = getThis() instanceof PreviewController
                                ? ((PreviewController) getThis()).viewLevel : 0;
                        Window w = new PreviewWindow(
                                new PreviewController(app, viewLevel + 1,
                                        new LocationModel(app.dbConnector.getLocationByViewId(viewId),
                                                app.dbConnector.getLocationList()),
                                        new ViewModel(viewId,
                                                app.dbConnector.getLinkById(
                                                        app.dbConnector.addLink(viewId, event.getRelativeX() / width,
                                                                event.getRelativeY() / height, viewId)),
                                                viewModel)));
                        w.setModal(true);
                        app.getMainWindow().addWindow(w);
                        w.setCloseShortcut(KeyCode.ESCAPE, null);
                        w.focus();
                    }
                }
            }
        };
    }

    public ViewModelListener getViewModelListener() {
        return new ViewModelListener() {
            public void viewClosing() {
                System.out.println("Its business of view part to close windows");
            }

            public void viewChanged() {
                LinkModel lm = viewModel.editLink;
                if (lm != null && lm.getId() > 0) {
                    lm.viewRefId = viewModel.getViewId();
                    app.dbConnector.editLink(lm);
                }
            }
        };
    }

    public ViewController getThis() {
        return this;
    }
}