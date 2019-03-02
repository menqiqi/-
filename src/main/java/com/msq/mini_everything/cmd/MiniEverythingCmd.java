package com.msq.mini_everything.cmd;

import com.msq.mini_everything.core.MiniEverythingManager;
import com.msq.mini_everything.core.model.Condition;
import com.msq.mini_everything.core.model.Thing;

import java.util.List;
import java.util.Scanner;

public class MiniEverythingCmd {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        //欢迎
        welcome();

        //统一调度器
        MiniEverythingManager manager = MiniEverythingManager.getInstance();

        //启动后台清理线程
        manager.startBackgroundClearThread();

        //交互式
        interactive(manager);
    }

    private static void interactive(MiniEverythingManager manager){
        while (true){
            System.out.println("everything >>");
            String input = scanner.nextLine();
            //优先处理search
            if (input.startsWith("search")){
                //search name [file_type]
                String[] values = input.split(" ");
                if (values.length >= 2){
                    if (!values[0].equals("search")){
                        help();
                        continue;
                    }
                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);
                    if (values.length >= 3){
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    search(manager, condition);
                    continue;
                }else {
                    help();
                    continue;
                }
            }
            switch (input){
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    return;
                case "index":
                    index(manager);
                    break;
                    default:
                        help();
            }
        }
    }

    private static void search(MiniEverythingManager manager, Condition condition){
        //name fileType limit orderByAsc
        List<Thing> thingList = manager.search(condition);
        for (Thing thing : thingList){
            System.out.println(thing.getPath());
        }
    }

    private static void index(MiniEverythingManager manager){
        //统一调度器中的index
        new Thread(() -> manager.buildIndex()).start();
    }

    private static void quit(){}

    private static void welcome(){
        System.out.println("欢迎使用 mini everything");
    }

    private static void help(){}
}
