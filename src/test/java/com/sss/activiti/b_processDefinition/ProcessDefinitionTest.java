package com.sss.activiti.b_processDefinition;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
/**
 * 流程定义测试类
 */
public class ProcessDefinitionTest {

    @Autowired
    private RepositoryService repositoryService;    //与流程定义和部署对象相关的service

    //手动部署流程定义(从classpath)
    @Test
    public void deploymentProcessDefinition_classpath() {
        Deployment deploy = repositoryService.createDeployment()//创建一个部署对象
                .name("流程定义")
                .addClasspathResource("process/helloworld.bpmn")//从classpath的资源中加载，一次只能加载一个文件
                .addClasspathResource("process/helloworld.png")//从classpath的资源中加载，一次只能加载一个文件
                .deploy();//完成部署
        System.out.println("流程部署ID： " + deploy.getId());
        System.out.println("流程部署名称： " + deploy.getName());
    }

    //手动部署流程定义(从zip)
    @Test
    public void deploymentProcessDefinition_Zip() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("process/helloworld.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deploy = repositoryService.createDeployment()//创建一个部署对象
                .name("流程定义")//添加部署的名称
                .addZipInputStream(zipInputStream)//指定zip格式的文件完成部署
                .deploy();//完成部署
        System.out.println("流程部署ID： " + deploy.getId());
        System.out.println("流程部署名称： " + deploy.getName());
    }

    //查询流程定义
    @Test
    public void findProcessDefinition() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()//创建一个流程定义的查询
                //指定查询条件,where条件
//            .deploymentId()//使用部署对象ID查询
//            .processDefinitionId()//使用流程定义ID查询
//            .processDefinitionKey()//使用流程定义的key查询
//            .processDefinitionNameLike()//使用流程定义的名称模糊查询
                //排序
                .orderByProcessDefinitionVersion().asc()//按照版本的升序排列
//            .orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列

                //返回的结果集
                .list();//返回一个结果集,封装流程定义
//            .singleResult();//返回唯一结果集
//            .count();//返回结果集数量
//            .listPage(firstResult, maxResults);//分页查询
        if (null != list && list.size() > 0) {
            for (ProcessDefinition pd : list ){
                System.out.println("流程定义ID : "+ pd.getId());//流程定义的key:版本:随机生成数
                System.out.println("流程定义的名称 : "+ pd.getName());//对应helloworld.bpm文件中的name属性值
                System.out.println("流程定义的key : "+ pd.getKey());//对应helloworld.bpm文件中的id属性值
                System.out.println("流程定义的版本 : "+ pd.getVersion());//当流程定义的key值相同时,版本升级,默认1
                System.out.println("资源名称bpmn文件 : "+ pd.getResourceName());
                System.out.println("资源名称png文件 : "+ pd.getDiagramResourceName());
                System.out.println("部署对象ID : "+ pd.getDeploymentId());
                System.out.println("###########################");
            }
        }
    }

    //删除流程定义
    @Test
    public void deleteProcessDefinition() {
        //使用部署ID,完成删除
        String deployment = "15001";
        //不带级联的删除,只能删除没有启动的流程,如果流程启动,就会抛出异常
//        repositoryService.deleteDeployment(deployment);
        //级联删除,不管流程是否启动,都能删除
        repositoryService.deleteDeployment(deployment, true);
        System.out.println("删除成功");
    }

    //查看流程图
    @Test
    public void viewPic() {
        //将生成图片放到文件夹下
        String deploymentId = "20001";
        //获取图片资源名称
        List<String> list = repositoryService.getDeploymentResourceNames(deploymentId);
        //定义图片资源的名称
        String resourceName = "";
        if (null != list && list.size() > 0) {
            for (String name: list
                 ) {
                if (name.indexOf(".png") >= 0){
                    resourceName = name;
                }
            }
        }
        //获取图片输入流
        InputStream inputStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
        //将图片生成到D盘的目录下
        File file = new File("/Users/sss/sss.png");
        //将输入流的图片写到D盘下
        byte[] data = new byte[1024];
        int len = 0;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            while((len = inputStream.read(data)) != -1){
                fileOutputStream.write(data,0,len);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //附加功能,查询最新版本的流程定义
    @Test
    public void findLastVersionProcessDefinition() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion().asc()//使用流程定义的版本升序排列
                .list();
        /**
         * MAP<String,ProcessDefinition>
         *     map集合的key : 流程定义的key
         *     map集合的value : 流程定义的对象
         *     map集合的特点,当map集合key值相同的情况下,后一次的值将替换前一次的值
         */
        Map<String,ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();
        if (null != list && list.size() > 0){
            for (ProcessDefinition pd:list
                 ) {
                map.put(pd.getKey(),pd);
            }
        }
        List<ProcessDefinition> pdList = new ArrayList<>(map.values());
        if (null != pdList && pdList.size() > 0) {
            for (ProcessDefinition pd:pdList
                 ) {
                System.out.println("流程定义ID : "+ pd.getId());//流程定义的key:版本:随机生成数
                System.out.println("流程定义的名称 : "+ pd.getName());//对应helloworld.bpm文件中的name属性值
                System.out.println("流程定义的key : "+ pd.getKey());//对应helloworld.bpm文件中的id属性值
                System.out.println("流程定义的版本 : "+ pd.getVersion());//当流程定义的key值相同时,版本升级,默认1
                System.out.println("资源名称bpmn文件 : "+ pd.getResourceName());
                System.out.println("资源名称png文件 : "+ pd.getDiagramResourceName());
                System.out.println("部署对象ID : "+ pd.getDeploymentId());
                System.out.println("###########################");
            }
        }
    }

    //附加功能: 删除流程定义(删除key相同的所有不同版本的流程定义)
    @Test
    public void deleteProcessDefinitionByKey() {
        //流程定义的key
        String processDefinitionByKey = "helloworld";
        //先使用流程定义的key查询流程定义,查出所有的版本
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionByKey)//使用流程定义的key查询
                .list();
        //遍历获取每个流程定义的部署ID
        if (null != list && list.size() > 0) {
            for (ProcessDefinition pd:list
                 ) {
                //获取部署ID
                String deploymentId = pd.getDeploymentId();
                repositoryService.deleteDeployment(deploymentId,true);
            }
        }
    }
}
