package com.aiepoissac.busapp.core.test;

import com.aiepoissac.busapp.core.data.busarrival.BusStop;
import com.aiepoissac.busapp.core.data.busservices.BusServiceInfo;
import com.aiepoissac.busapp.core.data.busservices.BusStopInfo;

import java.util.InputMismatchException;
import java.util.Scanner;

public final class Main {

    public static Scanner s = new Scanner(System.in);

    public static void main(String[] args) {
        Main.initialiseData();
        Main.viewMainMenu();
    }

    private static void initialiseData() {
        BusServiceInfo.initialise();
    }

    private static void viewMainMenu() {
        while (true) {
            System.out.println("Bus App Test V2 (Made by LI YI CHENG)\n\nSelect an option: \n0. Exit\n1. Bus Arrival Time\n2. View Bus Stop Info\n3. View Bus Service Info\n4. View MRT/LRT Station Info\n");
            int i;
            try {
                i = Integer.parseInt(s.nextLine());
                if (i == 0) {
                    System.exit(0);
                } else if (i == 1) {
                    viewBusArrivalInfo();
                } else if (i == 2) {
                    viewBusStopInfo();
                } else if (i == 3) {
                    viewBusServiceInfo();
//                } else if (i == 4) {
//                    viewTrainStationInfo();
                } else {
                    System.out.println("Not a valid option\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Not a valid integer\n");
            }
        }
    }

    private static void viewBusArrivalInfo() {
        while (true) {
            System.out.println("Enter Bus Stop Code (type -1 to exit): ");
            String busStopCode = s.nextLine();
            if (busStopCode.equals("-1")){
                break;
            }
            String info = String.valueOf(BusStop.getBusArrival(busStopCode));
            if (info != null) {
                System.out.println(info + "\n");
            } else {
                System.out.println("No such bus stop\n");
            }
        }
    }

    private static void viewBusStopInfo() {
        while (true) {
            try {
                System.out.println("Enter Bus Stop Code (type -1 to exit): ");
                int busStopCode = Integer.parseInt(s.nextLine());
                if (busStopCode == -1){
                    break;
                }
                String info = BusStopInfo.getBusStopInfoString(busStopCode, 500, true);
                if (info != null) {
                    System.out.println(info + "\n");
                } else {
                    System.out.println("No such bus stop\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("No such bus stop\n");
            }
        }
    }

    private static void viewBusServiceInfo() {
        while (true) {
            System.out.println("Enter Bus Service (case sensitive, type -1 to exit): ");
            String serviceNo = s.nextLine();
            if (serviceNo.equals("-1")){
                break;
            }
            String info = BusServiceInfo.getBusServiceInfoString(serviceNo, true);
            if (info != null) {
                System.out.println(info + "\n");
            } else {
                System.out.println("No such bus service\n");
            }
        }
    }
}
