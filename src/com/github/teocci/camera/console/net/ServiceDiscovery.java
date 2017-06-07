package com.github.teocci.camera.console.net;

import com.github.teocci.camera.console.controllers.ConnectController;
import com.github.teocci.camera.console.controllers.ConnectController;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-May-19
 */
public class ServiceDiscovery
{

    public static void main(String[] args, ConnectController connectController) throws InterruptedException
    {
//        try {
//
//            JmDNS jmDNS = JmDNS.create();//InetAddress.getLocalHost());
//
//            // connectController.setConnectResult(jmDNS.getHostName() + ", " + jmDNS.getName());
//
//         //   System.out.println("leng; " + args.length);
//        //    for ( int i=0; i < args.length; i++ ) {
//          //          System.out.println("for-");
///*            Logger logger = Logger.getLogger(jmDNS.getClass().getName());
//            ConsoleHandler handler = new ConsoleHandler();
//            logger.addHandler(handler);
//            logger.setLevel(Level.FINER);
//            handler.setLevel(Level.FINER);
//*/
//
//            int b;
//            while ((b=System.in.read()) != -1);
//            ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", "foo", 1268, 0, 0, "path=index.html");
//            jmDNS.registerService(serviceInfo);
//            jmDNS.printServices();
//
//            jmDNS.addServiceListener("_http._tcp.local.", new ServiceListener() {
//            //jmDNS.addServiceListener(args[i], new ServiceListener() {
//                @Override
//                public void serviceAdded(ServiceEvent event) {
//                    jmDNS.requestServiceInfo(event.getType(), event.getName());
//                    System.out.println("Service added: " + event.getInfo());
//                }
//
//                @Override
//                public void serviceRemoved(ServiceEvent event) {
//                    System.out.println("Service removed: " + event.getInfo());
//                }
//
//                @Override
//                public void serviceResolved(ServiceEvent event) {
//                    System.out.println("Service resolved: " + event.getInfo());
//                    connectController.setConnectResult(event.getInfo().getName());
//              /*     ServiceInfo[] infos = jmDNS.list("http._tcp.local.");
//                    for (int i=0; i < infos.length; i++) {
//                        System.out.println(infos[i]);
//                        connectController.setConnectResult(infos[i].getName());
//                    }*/
//
//                }
//            });
//
//
//        } catch (UnknownHostException e ) {
//            System.out.println(e.getMessage());
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//
//
    }

}
