package com.example.guib_compiler;

import static com.google.auto.common.MoreElements.getPackage;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.swing.text.View;
import javax.tools.Diagnostic;

import com.example.guib_annotation.Bind;
import com.example.guib_annotation.DoSomething_logic;
import com.example.guib_annotation.ViewSet;
import com.example.guib_annotation.api;
import com.example.guib_annotation.setContext;
import com.example.guib_annotation.viewonclick;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
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

    public static String classFoot = "_Binding";


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
        typeSet.add(DoSomething_logic.class.getCanonicalName());
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
            for (Element element : roundEnv.getElementsAnnotatedWith(DoSomething_logic.class)){
                bindElement.add(element);
                praseElement(element, DoSomething_logic.class);
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
            return true;
        }
        return false;
    }

    /**
     *
     * @param messsage
     */
    public void generateCode(ClassMesssage messsage) {
        TypeSpec.Builder binding = messsage.typeBuilder;
        generateCodeViewOnclick(messsage);
        generateCodeViewSet(messsage);
        // 实现构造函数 初始化bind类
        binding.addMethod(createBindingConstructor(messsage));
        generateCodeApi(messsage);
    }

    // 初始化api消息中心
    public void generateCodeApi(ClassMesssage messsage) {
        TypeSpec.Builder binding = messsage.typeBuilder;
        String keyHead = messsage.className;
        binding.addSuperinterface(ClassName.get("com.example.viewsethelp.bindhelp.apicenter","ApiExecutor"));
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
                if (modifiers.contains(Modifier.PUBLIC)) {
                    methodBuilder.addStatement("if (api.what() == $L) {\n"
                            + "    mContext.$N(api);\n"
                            + "}", bindAnnotation.what(), element.getSimpleName());
                } else {
                    ClassName methodClass = ClassName.get("java.lang.reflect","Method");
                    methodBuilder.addStatement("if (api.what() == $L) {\n"
                            + "    try {\n"
                            + "        $T ms = mContext.getClass().getDeclaredMethod(\"$L\");\n"
                            + "        ms.setAccessible(true);\n"
                            + "        ms.invoke(mContext, api);\n"
                            + "    } catch (Exception e) {\n"
                            + "        e.printStackTrace();\n"
                            + "    }\n"
                            + "}",bindAnnotation.what(), methodClass, element.getSimpleName());
                }
            }
        }
        binding.addMethod(methodBuilder.build());

        MethodSpec.Builder targetMethodBuilder = MethodSpec
                .methodBuilder("Target")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(String.class);
        targetMethodBuilder.addStatement("return \"$L\"", messsage.className.replace(classFoot, ""));
        binding.addMethod(targetMethodBuilder.build());
    }

    public void generateCodeViewSet(ClassMesssage messsage) {
        TypeSpec.Builder binding = messsage.typeBuilder;
        String keyHead = messsage.className;
        List<Element> elements = elementHashMap.get(keyHead + ViewSet.class.getSimpleName());
        if (elements != null && elements.size() > 0) {
            MethodSpec.Builder methodBuilder = MethodSpec
                    .methodBuilder("ViewSet")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(messsage.packageName,messsage.className.replace(classFoot, "")), "mContext", Modifier.FINAL)
                    .addParameter(ClassName.get("android.view","View"), "rootView")
                    .returns(TypeName.VOID);
            for (Element element : elements) {

                ViewSet bindAnnotation = element.getAnnotation(ViewSet.class);
                Set<Modifier> modifiers = element.getModifiers();
                String L = "$L";
                if (modifiers.contains(Modifier.PUBLIC)) {
                    L = "mContext.$L";
                } else {
                    ClassName fieldClass = ClassName.get("java.lang.reflect","Field");
                    methodBuilder.addStatement("String $L = \"\";\n"
                            + "try {\n"
                            + "    $T ms = mContext.getClass().getDeclaredField(\"$L\");\n"
                            + "    $L = ms.get(mContext) + \"\";\n"
                            + "} catch (Exception e) {\n"
                            + "    e.printStackTrace();\n"
                            + "}",element.getSimpleName(), fieldClass, element.getSimpleName(), element.getSimpleName());
                }
                if (bindAnnotation.type() == ViewSet.ViewType.TV) {
                    ClassName textViewClass = ClassName.get("android.widget","TextView");
                    methodBuilder.addStatement("try {\n"
                                    + "     ( ($T) rootView.findViewById($L) ).setText(\"$L\"+"
                                    + L +"+\"$L\");\n"
                                    + "} catch (Exception e) {\n"
                                    + "     e.printStackTrace();\n"
                                    + "}",textViewClass, bindAnnotation.id()
                            , bindAnnotation.head(), element.getSimpleName(), bindAnnotation.foot());
                } else if(bindAnnotation.type() == ViewSet.ViewType.IV) {
                    ClassName imageViewClass = ClassName.get("android.widget","ImageView");
                    ClassName glideClass = ClassName.get("com.bumptech.glide","Glide");
                    methodBuilder.addStatement("try {\n"
                            + "     $T.with(rootView.getContext()).load("+L+").into(($T) "
                            + "rootView.findViewById($L));\n"
                            + "} catch (Exception e) {\n"
                            + "     e.printStackTrace();\n"
                            + "}",glideClass, element.getSimpleName(),imageViewClass,bindAnnotation.id());
                }
            }

            binding.addMethod(methodBuilder.build());
        }
    }

    public void generateCodeViewOnclick(ClassMesssage messsage) {
        TypeSpec.Builder binding = messsage.typeBuilder;
        String keyHead = messsage.className;
        List<Element> elements = elementHashMap.get(keyHead + viewonclick.class.getSimpleName());
        if (elements != null && elements.size() > 0) {
            MethodSpec.Builder methodBuilder = MethodSpec
                    .methodBuilder("setClickListener")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(messsage.packageName,messsage.className.replace(classFoot, "")), "mContext", Modifier.FINAL)
                    .addParameter(ClassName.get("android.view","View"), "rootView")
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
                if (modifiers.contains(Modifier.PUBLIC)) {
                    methodBuilder.addStatement("rootView.findViewById($L)\n"
                            + "    .setOnClickListener(new $T.OnClickListener() {\n"
                            + "         @Override\n"
                            + "         public void onClick($T v) {\n"
                            + "             mContext.$N();\n"
                            + "         }\n"
                            + "    })",bindAnnotation.value(), viewclass, viewclass, element.getSimpleName());
                } else {
                    ClassName methodClass = ClassName.get("java.lang.reflect","Method");
                    methodBuilder.addStatement("rootView.findViewById($L)\n"
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
                            + "    })",bindAnnotation.value(), viewclass, viewclass, methodClass, element.getSimpleName());
                }
            }

            binding.addMethod(methodBuilder.build());
        }
    }

    // 构造函数
    private MethodSpec createBindingConstructor(ClassMesssage messsage) {
        TypeSpec.Builder binding = messsage.typeBuilder;
        binding.addField(ClassName.get(messsage.packageName,messsage.className.replace(classFoot, ""))
                , "mContext", Modifier.PUBLIC);
        MethodSpec.Builder methodBuilder = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(messsage.packageName,messsage.className.replace(classFoot, "")), "mContext", Modifier.FINAL)
                .addParameter(ClassName.get("android.view","View"), "rootView");
        methodBuilder.addStatement("this.mContext = mContext");
        ClassName methodClass = ClassName.get("com.example.viewsethelp.bindhelp","ViewSetHelp");
        methodBuilder.addStatement("$T.register(this)", methodClass);
        for (MethodSpec methodSpec : binding.build().methodSpecs) {
            methodBuilder.addStatement("$N(mContext, rootView)", methodSpec);
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
        ClassName bindingClassName = ClassName.get(packageName, className + classFoot);

        ClassMesssage messsage = classHashMap.get(bindingClassName.packageName() + bindingClassName.simpleName());
        if (messsage == null) {
            messsage = new ClassMesssage();
            messsage.typeBuilder = TypeSpec.classBuilder(bindingClassName.simpleName()).addModifiers(Modifier.PUBLIC);
            messsage.className = bindingClassName.simpleName();
            messsage.packageName = bindingClassName.packageName();
            classHashMap.put(bindingClassName.packageName() + bindingClassName.simpleName(), messsage);
        }
        List<Element> elements = elementHashMap.get(bindingClassName.simpleName() + annotation.getSimpleName());
        if (elements == null) {
            elements = new ArrayList<>();
        }
        elements.add(element);
        elementHashMap.put(bindingClassName.simpleName() + annotation.getSimpleName(), elements);
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