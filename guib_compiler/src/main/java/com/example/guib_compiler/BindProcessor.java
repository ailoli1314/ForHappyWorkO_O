package com.example.guib_compiler;

import static com.google.auto.common.MoreElements.getPackage;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import com.example.guib_annotation.Bind;
import com.example.guib_annotation.DoSomething;
import com.example.guib_annotation.ViewSet;
import com.example.guib_annotation.api;
import com.example.guib_annotation.setContext;
import com.example.guib_annotation.viewonclick;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * @author Created by qiyei2015 on 2018/4/15.
 * @version: 1.0
 * @email: 1273482124@qq.com
 * @description: Bind注解处理器
 */
@AutoService(Processor.class)
public class BindProcessor extends AbstractProcessor {

    /**
     * java源文件操作相关类，主要用于生成java源文件
     */
    private Filer mFiler;
    /**
     * 注解类型工具类，主要用于后续生成java源文件使用
     * 类为TypeElement，变量为VariableElement，方法为ExecuteableElement
     */
    private Elements mElementsUtils;
    /**
     * 日志打印，类似于log,可用于输出错误信息
     */
    private Messager mMessager;

    public static String classFoot = "_Binder";


    /**
     * 初始化，主要用于初始化各个变量
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementsUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
    }

    /**
     * 支持的注解类型
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {

        Set<String> typeSet = new LinkedHashSet<>();

        typeSet.add(Bind.class.getCanonicalName());
        typeSet.add(DoSomething.class.getCanonicalName());
        typeSet.add(setContext.class.getCanonicalName());
        typeSet.add(viewonclick.class.getCanonicalName());
        typeSet.add(ViewSet.class.getCanonicalName());

        return typeSet;
    }

    /**
     * 支持的版本
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     *
     * 1.搜集信息
     * 2.生成java源文件
     * @param annotations
     * @param roundEnv
     * @return
     */
    int count = 0;
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!annotations.isEmpty()){
            //获取Bind注解类型的元素，这里是类类型TypeElement
            List<Element> bindElement = new ArrayList<>();
            for (Element element : roundEnv.getElementsAnnotatedWith(Bind.class)){
                bindElement.add(element);
                praseElement(element, Bind.class);
            }
            for (Element element : roundEnv.getElementsAnnotatedWith(setContext.class)){
                bindElement.add(element);
                praseElement(element, setContext.class);
            }
            for (Element element : roundEnv.getElementsAnnotatedWith(DoSomething.class)){
                bindElement.add(element);
                praseElement(element, DoSomething.class);
            }
            for (Element element : roundEnv.getElementsAnnotatedWith(viewonclick.class)){
                bindElement.add(element);
                praseElement(element, viewonclick.class);
            }
            for (Element element : roundEnv.getElementsAnnotatedWith(ViewSet.class)){
                bindElement.add(element);
                praseElement(element, ViewSet.class);
            }
            for (Element element : roundEnv.getElementsAnnotatedWith(api.class)){
                bindElement.add(element);
                praseElement(element, api.class);
            }
            if (bindElement == null || bindElement.size() < 1) {
                return true;
            }

            // 给使用了对应注解的类 分别实现bind类
            for (Map.Entry<String, ClassMesssage> entry : classHashMap.entrySet()) {
                String packageName_key = entry.getKey();
                ClassMesssage messsage = entry.getValue();
                generateCode(messsage);
                //创建javaFile文件对象
                JavaFile javaFile = JavaFile.builder(messsage.packageName,messsage.typeBuilder.build()).build();
                //写入源文件
                if (!addedFile.contains(javaFile)) {
                    try {
                        javaFile.writeTo(mFiler);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    addedFile.add(javaFile);
                }
            }
            generateBinderFactory();
            return true;
        }
        return false;
    }

    String binderFactoryName = "BinderFactory";

    /**
     * 生成待绑定类的工厂类
     */
    public void generateBinderFactory() {
        String packageName = "empty_zz";
        TypeSpec.Builder factoryBuild = TypeSpec.classBuilder(binderFactoryName).addModifiers(Modifier.PUBLIC);
        TypeName contextClass = ClassName.get("com.example.viewsethelp.bindhelp","iViewSet");
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("getBinder")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextClass, "mContext")
                .addParameter(String.class, "className")
                .returns(contextClass);
        for (Map.Entry<String, ClassMesssage> entry : classHashMap.entrySet()) {
            ClassMesssage messsage = entry.getValue();
            packageName = messsage.packageName;
            String contextName = messsage.className.replace(classFoot, "");
            ClassName contextC = ClassName.get(packageName,contextName);
            methodBuilder.addStatement("if (className.equals(\"$L\")) {"
                    + " return new $L_Binder(($L) mContext, mContext.rootView()); "
                    + "}", contextName, contextC, contextC);
        }
        methodBuilder.addStatement("return null");
        factoryBuild.addMethod(methodBuilder.build());

        //创建javaFile文件对象
        JavaFile javaFile = JavaFile.builder(packageName, factoryBuild.build()).build();
        //写入源文件
        if (!addedFile.contains(javaFile)) {
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            addedFile.add(javaFile);
        }
    }

    /**
     * 生成注解方法
     * @param messsage
     */
    public void generateCode(ClassMesssage messsage) {
        String keyHead = messsage.className;
        List<Element> elements = null;

        TypeSpec.Builder binder = messsage.typeBuilder;

        elements = elementHashMap.get(keyHead + DoSomething.class.getSimpleName());
        if (elements != null && !elements.isEmpty()) {
            generateCodeDoSomething(messsage);
        }

        elements = elementHashMap.get(keyHead + viewonclick.class.getSimpleName());
        if (elements != null && !elements.isEmpty()) {
            generateCodeViewOnclick(messsage);
        }

        elements = elementHashMap.get(keyHead + api.class.getSimpleName());
        if (elements != null && !elements.isEmpty()) {
            generateCodeApi(messsage);
        }

        generateCodeViewSet(messsage);
        // 实现构造函数 初始化bind类
        binder.addMethod(createbinderConstructor(messsage));
        generateCodeInterfaceViewSet(messsage);
    }

    public void generateCodeDoSomething(ClassMesssage messsage) {
        TypeSpec.Builder binder = messsage.typeBuilder;
        String keyHead = messsage.className;
        List<Element> elements = elementHashMap.get(keyHead + DoSomething.class.getSimpleName());
        if (elements != null && elements.size() > 0) {
            MethodSpec.Builder methodBuilder = MethodSpec
                    .methodBuilder("doSomething")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID);
            for (Element element : elements) {
                Set<Modifier> modifiers = element.getModifiers();
                ClassName methodClass = ClassName.get("java.lang.reflect","Method");
                boolean isPublic = false;
                ClassName contextClass = null;
                if (modifiers.contains(Modifier.PUBLIC)) {
                    isPublic = true;
                    contextClass = ClassName.get(messsage.packageName,messsage.className.replace(classFoot, ""));
                }
                if (!isPublic || contextClass == null) {
                    methodBuilder.addStatement(
                              "try {\n"
                            + "    $T ms = mContext.getClass().getDeclaredMethod(\"$L\");\n"
                            + "    ms.setAccessible(true);\n"
                            + "    ms.invoke(mContext);\n"
                            + "} catch (Exception e) {\n"
                            + "    e.printStackTrace();\n"
                            + "}\n", methodClass, element.getSimpleName());
                } else {
                    methodBuilder.addStatement(
                              "try {\n"
                            + "    (($T)mContext).$L();\n"
                            + "} catch (Exception e) {\n"
                            + "    e.printStackTrace();\n"
                            + "}", contextClass, element.getSimpleName());
                }
            }
            binder.addMethod(methodBuilder.build());
        }
    }

    private void generateCodeInterfaceViewSet(ClassMesssage messsage) {
        TypeSpec.Builder binder = messsage.typeBuilder;
        String keyHead = messsage.className;
        TypeName iViewSet = ClassName.get("com.example.viewsethelp.bindhelp","iViewSet");

        binder.addSuperinterface(iViewSet);

        MethodSpec.Builder bindMethodBuilder = MethodSpec
                .methodBuilder("bindInstance")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(iViewSet, "mContext")
                .addAnnotation(Override.class)
                .returns(TypeName.VOID);
        binder.addMethod(bindMethodBuilder.build());

        MethodSpec.Builder getBindMethodBuilder = MethodSpec
                .methodBuilder("getBinder")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(iViewSet);
        getBindMethodBuilder.addStatement("return this");
        binder.addMethod(getBindMethodBuilder.build());

        // value_change在generateCodeViewSet中实现：generateValueChange

        ClassName viewclass = ClassName.get("android.view","View");
        MethodSpec.Builder rootViewMethodBuilder = MethodSpec
                .methodBuilder("rootView")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(viewclass);
        rootViewMethodBuilder.addStatement("return mRootView");
        binder.addMethod(rootViewMethodBuilder.build());

        MethodSpec.Builder destoryMethodBuilder = MethodSpec
                .methodBuilder("destory")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.VOID);
        List<Element> elements = elementHashMap.get(keyHead + api.class.getSimpleName());
        if (elements != null && elements.size() > 0) {
            destoryMethodBuilder.addStatement("ViewSetHelp.unRegister(this)");
        }
        destoryMethodBuilder.addStatement("mContext = null");
        destoryMethodBuilder.addStatement("mRootView = null");
        binder.addMethod(destoryMethodBuilder.build());
    }

    // 初始化api消息中心
    public void generateCodeApi(ClassMesssage messsage) {
        TypeSpec.Builder binder = messsage.typeBuilder;
        String keyHead = messsage.className;
        binder.addSuperinterface(ClassName.get("com.example.viewsethelp.bindhelp.apicenter","ApiExecutor"));

        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("execute")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get("com.example.viewsethelp.bindhelp.apicenter","Api"), "api")
                .returns(TypeName.VOID);
        List<Element> elements = elementHashMap.get(keyHead + api.class.getSimpleName());
        methodBuilder.addStatement("if (api == null) {\n"
                + "    return;\n"
                + "}");
        if (elements != null && elements.size() > 0) {
            for (Element element : elements) {
                api bindAnnotation = element.getAnnotation(api.class);
                Set<Modifier> modifiers = element.getModifiers();
                boolean isPublic = false;
                ClassName methodClass = ClassName.get("java.lang.reflect","Method");
                ClassName contextClass = null;
                if (modifiers.contains(Modifier.PUBLIC)) {
                    isPublic = true;
                    contextClass = ClassName.get(messsage.packageName,messsage.className.replace(classFoot, ""));
                }
                if (!isPublic || contextClass == null) {
                    methodBuilder.addStatement("if (api.what() == $L) {\n"
                            + "    try {\n"
                            + "        $T ms = mContext.getClass().getDeclaredMethod(\"$L\", Api.class);\n"
                            + "        ms.setAccessible(true);\n"
                            + "        ms.invoke(mContext, api);\n"
                            + "    } catch (Exception e) {\n"
                            + "        e.printStackTrace();\n"
                            + "    }\n"
                            + "}",bindAnnotation.what(), methodClass, element.getSimpleName());
                } else {
                    methodBuilder.addStatement("if (api.what() == $L) {\n"
                            + "    try {\n"
                            + "        (($T)mContext).$L(api);\n"
                            + "    } catch (Exception e) {\n"
                            + "        e.printStackTrace();\n"
                            + "    }\n"
                            + "}",bindAnnotation.what(), contextClass, element.getSimpleName());
                }
            }
        }
        binder.addMethod(methodBuilder.build());

        MethodSpec.Builder targetMethodBuilder = MethodSpec
                .methodBuilder("Target")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Object.class);
        ClassName mainClassName = ClassName.get(messsage.packageName,messsage.className.replace(classFoot, ""));
        targetMethodBuilder.addStatement("return (($T)mContext).getClass()", mainClassName);
        binder.addMethod(targetMethodBuilder.build());
    }

    public void generateCodeViewSet(ClassMesssage messsage) {
        TypeSpec.Builder binder = messsage.typeBuilder;
        String keyHead = messsage.className;
        List<Element> elements = elementHashMap.get(keyHead + ViewSet.class.getSimpleName());
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("ViewSet")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID);
        // 保存viewset注释生成的textView，imageView设置方法
        List<String> methodNames = new ArrayList<>();
        if (elements != null && elements.size() > 0) {
            for (Element element : elements) {

                ViewSet bindAnnotation = element.getAnnotation(ViewSet.class);
                Set<Modifier> modifiers = element.getModifiers();
                ClassName fieldClass = ClassName.get("java.lang.reflect","Field");
                boolean isPublic = false;
                ClassName contextClass = null;
                if (modifiers.contains(Modifier.PUBLIC)) {
                    isPublic = true;
                    contextClass = ClassName.get(messsage.packageName,messsage.className.replace(classFoot, ""));
                }
                if (!isPublic || contextClass == null) {
                    methodBuilder.addStatement(""
                            + "try {\n"
                            + "    $T ms = mContext.getClass().getDeclaredField(\"$L\");\n"
                            + "    ms.setAccessible(true);\n"
                            + "    Class<?> fieldType = ms.getType();\n"
                            + "    Object $L = fieldType.cast(ms.get(mContext))"
                            , fieldClass, element.getSimpleName(), element.getSimpleName());
                } else {
                    methodBuilder.addStatement(""
                                    + "try {\n"
                                    + "    Object $L = (($T)mContext).$L"
                            , element.getSimpleName(), contextClass, element.getSimpleName());
                }
                if (bindAnnotation.type() == ViewSet.ViewType.TV) {
                    tvChange(bindAnnotation, binder, element.getSimpleName().toString(), methodNames);
                    methodBuilder.addStatement(
                                      "        $L_tv_change($L);\n"
                                    + "} catch (Exception e) {\n"
                                    + "    e.printStackTrace();\n"
                                    + "}", element.getSimpleName(), element.getSimpleName());
                } else if(bindAnnotation.type() == ViewSet.ViewType.IV) {
                    ivChange(bindAnnotation, binder, element.getSimpleName().toString(), methodNames);
                    methodBuilder.addStatement(
                                      "        $L_iv_change($L);\n"
                                    + "} catch (Exception e) {\n"
                                    + "    e.printStackTrace();\n"
                                    + "}"
                            ,element.getSimpleName(), element.getSimpleName());
                }
            }
        }
        generateValueChange(methodNames, binder);
        binder.addMethod(methodBuilder.build());
    }

    private void generateValueChange(List<String> methodNames, TypeSpec.Builder binder) {
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("value_change")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(String.class, "name")
                .addParameter(Object.class, "value")
                .returns(TypeName.VOID);
        methodBuilder.addStatement("if (name == null) { "
                + "return;"
                + "}");
        for (String name : methodNames) {
            methodBuilder.addStatement("if (name.equals(\"$L\")) { "
                    + "$L_change(value);"
                    + "}", name, name);
        }
        binder.addMethod(methodBuilder.build());
    }

    /**
     * 生成textView设置方法
     * @param viewSet
     * @param binder
     * @param name
     * @param methodNames
     */
    private void tvChange(ViewSet viewSet, TypeSpec.Builder binder, String name, List<String> methodNames) {
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder(name + "_tv_change")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class, "value")
                .returns(TypeName.VOID);
        ClassName textViewClass = ClassName.get("android.widget","TextView");
        methodBuilder.addStatement("try {\n"
                        + "     ( ($T) mRootView.findViewById($L) ).setText(\"$L\"+"
                        +"value +\"$L\");\n"
                        + "} catch (Exception e) {\n"
                        + "     e.printStackTrace();\n"
                        + "}",textViewClass, viewSet.id()
                , viewSet.head(), viewSet.foot());
        binder.addMethod(methodBuilder.build());
        methodNames.add(name + "_tv");
    }

    /**
     * 生成imageView设置方法
     * @param viewSet
     * @param binder
     * @param name
     * @param methodNames
     */
    private void ivChange(ViewSet viewSet, TypeSpec.Builder binder, String name, List<String> methodNames) {

        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder(name + "_iv_change")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class, "value")
                .returns(TypeName.VOID);
        ClassName imageViewClass = ClassName.get("android.widget","ImageView");
        ClassName glideClass = ClassName.get("com.bumptech.glide","Glide");
        ClassName drawableClass = ClassName.get("android.graphics.drawable","Drawable");
        ClassName stringClass = ClassName.get("java.lang","String");
        methodBuilder.addStatement("try{\n"
                        +  "   if (value instanceof $L) {\n"
                        + "      $T.with(mRootView.getContext()).load(value).into(($T) mRootView.findViewById($L));\n"
                        + "    } else if (value instanceof $L) {\n"
                        + "      ((ImageView) mRootView.findViewById($L)).setImageDrawable(($L) value);\n"
                        + "    }\n"
                        + "} catch (Exception e) {\n"
                        + "     e.printStackTrace();\n"
                        + "}"
                ,stringClass, glideClass, imageViewClass,viewSet.id(),drawableClass,viewSet.id(),drawableClass);


        binder.addMethod(methodBuilder.build());
        methodNames.add(name + "_iv");
    }

    public void generateCodeViewOnclick(ClassMesssage messsage) {
        TypeSpec.Builder binder = messsage.typeBuilder;
        String keyHead = messsage.className;
        List<Element> elements = elementHashMap.get(keyHead + viewonclick.class.getSimpleName());
        if (elements != null && elements.size() > 0) {
            MethodSpec.Builder methodBuilder = MethodSpec
                    .methodBuilder("setClickListener")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID);
            for (Element element : elements) {
                List<? extends VariableElement> parameters = ((ExecutableElement) element)
                        .getParameters();
                for (int i = 0; i < parameters.size(); i++) {
                    String paramterType =parameters.get(i).asType().toString(); // 返回的形式例如 java.lang.String
                    String name = parameters.get(i).getSimpleName().toString();
                    if (name.equals("Int")) {
                    } else if (name.equals("String")){
                    }
                }
                viewonclick bindAnnotation = element.getAnnotation(viewonclick.class);
                ClassName viewclass = ClassName.get("android.view","View");
                Set<Modifier> modifiers = element.getModifiers();
                ClassName methodClass = ClassName.get("java.lang.reflect","Method");
                boolean isPublic = false;
                ClassName contextClass = null;
                if (modifiers.contains(Modifier.PUBLIC)) {
                    isPublic = true;
                    contextClass = ClassName.get(messsage.packageName,messsage.className.replace(classFoot, ""));
                }
                if (!isPublic || contextClass == null) {
                    methodBuilder.addStatement("try {"
                            + "mRootView.findViewById($L)\n"
                            + "    .setOnClickListener(new $T.OnClickListener() {\n"
                            + "         @Override\n"
                            + "         public void onClick($T v) {\n"
                            + "             try {\n"
                            + "                  $T ms = mContext.getClass().getDeclaredMethod(\"$L\");\n"
                            + "                  ms.setAccessible(true);\n"
                            + "                  ms.invoke(mContext);\n"
                            + "             } catch (Exception e) {\n"
                            + "                  e.printStackTrace();\n"
                            + "             }\n"
                            + "         }\n"
                            + "    });\n" +
                            "} catch (Exception e) {\n"
                            + "     e.printStackTrace();\n"
                            + "}",bindAnnotation.id(), viewclass, viewclass, methodClass, element.getSimpleName());
                } else {
                    methodBuilder.addStatement("try {"
                            + "mRootView.findViewById($L)\n"
                            + "    .setOnClickListener(new $T.OnClickListener() {\n"
                            + "         @Override\n"
                            + "         public void onClick($T v) {\n"
                            + "             try {\n"
                            + "                  (($T)mContext).$L();\n"
                            + "             } catch (Exception e) {\n"
                            + "                  e.printStackTrace();\n"
                            + "             }\n"
                            + "         }\n"
                            + "    });\n" +
                            "} catch (Exception e) {\n"
                            + "     e.printStackTrace();\n"
                            + "}",bindAnnotation.id(), viewclass, viewclass, contextClass, element.getSimpleName());
                }
            }

            binder.addMethod(methodBuilder.build());
        }
    }

    // 构造函数
    private MethodSpec createbinderConstructor(ClassMesssage messsage) {
        TypeSpec.Builder binder = messsage.typeBuilder;
        TypeName contextClass = ClassName.get("com.example.viewsethelp.bindhelp","iViewSet");
        binder.addField(contextClass, "mContext", Modifier.PUBLIC);
        ClassName viewclass = ClassName.get("android.view","View");
        binder.addField(viewclass, "mRootView", Modifier.PUBLIC);
        MethodSpec.Builder methodBuilder = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextClass, "mContext", Modifier.FINAL)
                .addParameter(ClassName.get("android.view","View"), "rootView");
        methodBuilder.addStatement("this.mContext = mContext");
        methodBuilder.addStatement("this.mRootView = rootView");
        ClassName methodClass = ClassName.get("com.example.viewsethelp.bindhelp","ViewSetHelp");

        String keyHead = messsage.className;

        List<Element> elements = elementHashMap.get(keyHead + DoSomething.class.getSimpleName());
        if (elements != null && elements.size() > 0) {
            methodBuilder.addStatement("doSomething()");
        }

        elements = elementHashMap.get(keyHead + api.class.getSimpleName());
        if (elements != null && elements.size() > 0) {
            methodBuilder.addStatement("$T.register(this)", methodClass);
        }

        for (MethodSpec methodSpec : binder.build().methodSpecs) {
            if (methodSpec.name.equals("setClickListener") || methodSpec.name.equals("ViewSet")) {
                methodBuilder.addStatement("$N()", methodSpec);
            }
        }

        return methodBuilder.build();
    }

    /**
     * 按class+annotation类型 存储 方便取用
     * @param element
     * @param annotation
     */
    public void praseElement(Element element, Class<? extends Annotation> annotation) {
        // 获取父类元素的包名
        String packageName = getPackage(element).getQualifiedName().toString();
        // 获取父类元素的名称
        TypeElement enclosingElement;

        if (element instanceof TypeElement) {
            enclosingElement = (TypeElement) element;
        } else {
            enclosingElement = (TypeElement) element.getEnclosingElement();
        }

        String className = enclosingElement.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');
        // 即最终要生成的java类的名称
        ClassName binderClassName = ClassName.get(packageName, className + classFoot);

        ClassMesssage messsage = classHashMap.get(binderClassName.packageName() + binderClassName.simpleName());
        if (messsage == null) {
            messsage = new ClassMesssage();
            messsage.typeBuilder = TypeSpec.classBuilder(binderClassName.simpleName()).addModifiers(Modifier.PUBLIC);
            messsage.className = binderClassName.simpleName();
            messsage.packageName = binderClassName.packageName();
            classHashMap.put(binderClassName.packageName() + binderClassName.simpleName(), messsage);
        }
        List<Element> elements = elementHashMap.get(binderClassName.simpleName() + annotation.getSimpleName());
        if (elements == null) {
            elements = new ArrayList<>();
        }
        elements.add(element);
        elementHashMap.put(binderClassName.simpleName() + annotation.getSimpleName(), elements);
    }

    class ClassMesssage {
        TypeSpec.Builder typeBuilder;
        String packageName;
        String className;
    }

    List<JavaFile> addedFile = new ArrayList<>();

    // 待bind类
    private Map<String, ClassMesssage> classHashMap = new HashMap<>();
    // 待bind注解
    private Map<String, List<Element>> elementHashMap = new HashMap<>();
}