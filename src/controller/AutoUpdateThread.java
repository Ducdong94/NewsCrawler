/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.gameK.GetGameK_IndexNews;
import controller.gameK.GetGameK_NewsDetail;
import controller.kenh14.GetKenh14_IndexNews;
import controller.kenh14.GetKenh14_NewsDetail;
import controller.vnExpress.GetVnexpress_IndexNews;
import controller.vnExpress.GetVnexpress_NewsDetail;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dongvu
 */
public class AutoUpdateThread extends Thread {

    @Override
    public void run() {
        AutoUpdateThread n = new AutoUpdateThread();
        n.updateIndexNews();
        n.updateDetail();
    }

    public void updateIndexNews() {
        GetVnexpress_IndexNews getVnexpress = new GetVnexpress_IndexNews();
        GetKenh14_IndexNews getKenh14 = new GetKenh14_IndexNews();
        GetGameK_IndexNews getGameK = new GetGameK_IndexNews();

        getVnexpress.start();
      

        try {
            getVnexpress.join();
            getKenh14.join();
            getGameK.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(AutoUpdateThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void updateDetail() {
        GetVnexpress_NewsDetail vnExpressDetail = new GetVnexpress_NewsDetail();
        GetKenh14_NewsDetail kenh14Detail = new GetKenh14_NewsDetail();
        GetGameK_NewsDetail gameKDetail = new GetGameK_NewsDetail();

        vnExpressDetail.start();
        kenh14Detail.start();
        gameKDetail.start();

        try {
            vnExpressDetail.join();
            kenh14Detail.join();
            gameKDetail.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(AutoUpdateThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        AutoUpdateThread n = new AutoUpdateThread();
        n.updateIndexNews();
       
    }

}
