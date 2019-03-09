package com.msq.mini_everything.cmd;

import com.msq.mini_everything.config.MiniEverythingConfig;
import com.msq.mini_everything.core.MiniEverythingManager;
import com.msq.mini_everything.core.model.Condition;
import com.msq.mini_everything.core.model.Thing;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MiniEverythingCmd {

    private static Scanner scanner = new Scanner(System.in);

    private static ArrayList<String> historyList = new ArrayList<>();

    public static void main(String[] args) {

        //解析用户参数
        parseParams(args);

        //欢迎
        welcome();

        //统一调度器
        MiniEverythingManager manager = MiniEverythingManager.getInstance();

        //启动后台清理线程
        manager.startBackgroundClearThread();


//         启动监控
        manager.startFileSystemMonitor();

        //交互式
        interactive(manager);

    }


    private static void parseParams(String[] args) {
        MiniEverythingConfig config = MiniEverythingConfig.getInstance();
        /**
         * 处理参数：
         * 如果用户指定的参数格式不对，使用默认值即可
         */
        for (String param : args){
            String maxReturnParam = "--maxReturn=";
            if (param.startsWith(maxReturnParam)){
                int index = param.indexOf("=");
                String maxReturnStr = param.substring(index + 1);
                try{
                    int maxReturn = Integer.parseInt(maxReturnStr);
                    config.setMaxReturn(maxReturn);
                }catch (NumberFormatException e){
                    //如果用户指定的参数格式不对，使用默认值即可
                }
            }
            String deptOrderByAscParam = "--deptOrderByAsc=";
            if (param.startsWith(deptOrderByAscParam)){
                int index = param.indexOf("=");
                String deptOrderByAscStr = param.substring(index + 1);
                config.setDeptOrder(Boolean.parseBoolean(deptOrderByAscStr));
            }
            String includePathParam = "--includePath=";
            if (param.startsWith(includePathParam)){
                int index = param.indexOf("=");
                String includePathStr = param.substring(index + 1);
                String[] includePaths = includePathStr.split(";");
                if (includePaths.length > 0){
                    config.getIncludePath().clear();
                }
                for (String p : includePaths){
                    config.getIncludePath().add(p);
                }
            }
            String excludePathParam = "--excludePath=";
            if (param.startsWith(excludePathParam)){
                int index = param.indexOf("=");
                String excludePathStr = param.substring(index + 1);
                String[] excludePaths = excludePathStr.split(";");
                config.getExcludePath().clear();
                for (String p : excludePaths){
                    config.getExcludePath().add(p);
                }
            }
        }
    }

    private static void interactive(MiniEverythingManager manager){
        while (true){
            System.out.print("请输入：");
            String input = scanner.nextLine();
            historyList.add(input);
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
            //处理history，可以自己设置查找最近的几条历史命令
            if (input.startsWith("history")){
                String[] values = input.split(" ");
                if (values.length == 1){
                    //用户没有设置查找命令的个数
                    history(historyList.size()-1);
                    continue;
                }else if (values.length == 2){
                    //设置了个数
                    int num = Integer.parseInt(values[1]);
                    history(num);
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
        condition.setLimit(MiniEverythingConfig.getInstance().getMaxReturn());
        condition.setOrderByAsc(MiniEverythingConfig.getInstance().getDeptOrder());
        List<Thing> thingList = manager.search(condition);
        for (Thing thing : thingList){
            System.out.println(thing.getPath());
        }
    }

    private static void index(MiniEverythingManager manager){
        //统一调度器中的index
        new Thread(() -> manager.buildIndex()).start();
    }

    private static void quit(){
        System.out.println("bye-bye");
        System.exit(0);
    }

    private static void welcome(){
        System.out.println("欢迎使用 mini everything");
    }

    private static void help(){
        System.out.println("命令列表：");
        System.out.println("帮助 help");
        System.out.println("索引 index");
        System.out.println("查找历史命令 history [number]");
        System.out.println("搜索 search <name> [<file-Type> img | doc | bin | archive | other]");
        System.out.println("退出 quit");
    }

    private static void history(int num){
        //要查找的个数是从1开始的，但是遍历list是从0开始的，为了统一，先给num-1
        num = num - 1;
        if (num < 0){
            help();
        }else if (num <= historyList.size()-1){
            //输出最近的num条命令
            for (int i = historyList.size()-2-num; i <= historyList.size()-2; i++){
                System.out.println(historyList.get(i));
            }
        }else {
            for (int i = 0; i < historyList.size()-1; i++){
                System.out.println(historyList.get(i));
            }
        }
    }
}
