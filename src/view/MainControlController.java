/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.CompareOperator;
import entity.Filter;
import entity.News;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.ObjectModel;

/**
 * FXML Controller class
 *
 * @author dongvu
 */
public class MainControlController implements Initializable {

    private ObjectModel<News> model = new ObjectModel(News.class);
    private ArrayList<News> existNews = new ArrayList<>();
    @FXML
    private ListView<String> listNews;
    @FXML
    private Text title, desc;
    @FXML
    private TextFlow content;
    @FXML
    private Pane details;
    @FXML
    private Pagination pagination;
    private ObservableList<String> items;
    private int limit = 0;
    private int offset = 0;
    private int clickCount = 0;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void vnExpressEventHandle(MouseEvent event) {
        this.clickCount++;
        if (this.clickCount == 2) {
            details.setVisible(false);
            listNews.setVisible(true);
            Filter filter = new Filter();
            Filter.Conditions cond1 = filter.new Conditions();
            cond1.setCompare(CompareOperator.EQUAL);
            cond1.setCompareValue("1");
            filter.addField("status", cond1);

            Filter.Conditions cond2 = filter.new Conditions();
            cond2.setCompare(CompareOperator.EQUAL);
            cond2.setCompareValue("https://vnexpress.net/");
            filter.addField("source", cond2);

            existNews = model.getList(new News(), filter, limit, offset);
            items = FXCollections.observableArrayList();
            for (News New : existNews) {
                items.add(New.getTitle());
            }
            this.listNews.setItems(items);
            this.listNews.setCellFactory(param -> new ListCell<String>() {
                private ImageView imageView = new ImageView();

                @Override
                public void updateItem(String name, boolean empty) {
                    super.updateItem(name, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        for (News item : existNews) {
                            if (name.equals(item.getTitle())) {
                                setText(name);
                                setGraphic(imageView);

                                Thread setImageThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String linkImg = item.getImage().split("\\*")[0];
                                        imageView.setImage(new Image(linkImg));
                                        imageView.setFitWidth(100);
                                        imageView.setFitHeight(60);
                                        setGraphic(imageView);
                                    }
                                });
                                setImageThread.start();
                            }
                        }
                    }
                }
            }
            );
            this.clickCount = 0;
        }
    }

    @FXML
    private void kenh14EventHandle(MouseEvent event) {
        clickCount++;
        if (clickCount == 2) {

            details.setVisible(false);
            listNews.setVisible(true);
            Filter filter = new Filter();
            Filter.Conditions cond1 = filter.new Conditions();
            cond1.setCompare(CompareOperator.EQUAL);
            cond1.setCompareValue("1");
            filter.addField("status", cond1);

            Filter.Conditions cond2 = filter.new Conditions();
            cond2.setCompare(CompareOperator.EQUAL);
            cond2.setCompareValue("http://kenh14.vn/");
            filter.addField("source", cond2);

            existNews = model.getList(new News(), filter, limit, offset);
            items = FXCollections.observableArrayList();
            for (News New : existNews) {
                items.add(New.getTitle());
            }

            this.listNews.setItems(items);
            this.listNews.setCellFactory(param -> new ListCell<String>() {
                private ImageView imageView = new ImageView();

                @Override
                public void updateItem(String name, boolean empty) {
                    super.updateItem(name, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        for (News item : existNews) {
                            if (name.equals(item.getTitle())) {
                                setText(name);
                                setGraphic(imageView);

                                Thread setImageThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String linkImg = item.getImage().split("\\*")[0];
                                        imageView.setImage(new Image(linkImg));
                                        imageView.setFitWidth(100);
                                        imageView.setFitHeight(60);
                                        setGraphic(imageView);
                                    }
                                });
                                setImageThread.start();
                            }
                        }
                    }
                }
            }
            );
            clickCount = 0;
        }
    }

    @FXML
    private void gameKEventHandle(MouseEvent event) {
        clickCount++;
        if (clickCount == 2) {
            details.setVisible(false);
            listNews.setVisible(true);
            Filter filter = new Filter();
            Filter.Conditions cond1 = filter.new Conditions();
            cond1.setCompare(CompareOperator.EQUAL);
            cond1.setCompareValue("1");
            filter.addField("status", cond1);

            Filter.Conditions cond2 = filter.new Conditions();
            cond2.setCompare(CompareOperator.EQUAL);
            cond2.setCompareValue("http://gamek.vn/");
            filter.addField("source", cond2);

            existNews = model.getList(new News(), filter, limit, offset);
            items = FXCollections.observableArrayList();
            for (News New : existNews) {
                items.add(New.getTitle());
            }

            this.listNews.setItems(items);
            this.listNews.setCellFactory(param -> new ListCell<String>() {
                private ImageView imageView = new ImageView();

                @Override
                public void updateItem(String name, boolean empty) {
                    super.updateItem(name, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        for (News item : existNews) {
                            if (name.equals(item.getTitle())) {
                                setText(name);
                                setGraphic(imageView);

                                Thread setImageThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String linkImg = item.getImage().split("\\*")[0];
                                        imageView.setImage(new Image(linkImg));
                                        imageView.setFitWidth(100);
                                        imageView.setFitHeight(60);
                                        setGraphic(imageView);
                                    }
                                });
                                setImageThread.start();
                            }
                        }
                    }
                }
            }
            );
            clickCount = 0;
        }
    }

    @FXML
    private void listViewClicked(MouseEvent event) {
        clickCount++;
        String newsTitle = listNews.getSelectionModel().getSelectedItem();

        if (clickCount == 2 && !newsTitle.isEmpty()) {
            details.setVisible(true);
            listNews.setVisible(false);

            Filter filter = new Filter();
            Filter.Conditions cond = filter.new Conditions();
            cond.setCompare(CompareOperator.EQUAL);
            cond.setCompareValue(newsTitle);
            filter.addField("title", cond);

            News n = model.getObject(new News(), filter);

            title.setText(n.getTitle());
            desc.setText(n.getDescription());
            content.getChildren().add(new Text(n.getContent()));
            clickCount = 0;
        }
    }

}
