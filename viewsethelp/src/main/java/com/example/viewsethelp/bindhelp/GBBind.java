//package com.example.viewsethelp.bindhelp;
//
//import android.app.Activity;
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.blankj.utilcode.util.ToastUtils;
//import com.bumptech.glide.Glide;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.List;
//
//public class GBBind  {
//
//
//    public static void setcontext(Class classaction,Object classobj,Activity context){
//        try {
//            Field activity_IF=classaction.getDeclaredField("activity_IF");
//            activity_IF.setAccessible(true);
//            activity_IF.set(classobj,context);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        try {
//            Field activity_IF=classaction.getSuperclass().getDeclaredField("activity_IF");
//            activity_IF.setAccessible(true);
//            activity_IF.set(classobj,context);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void setcontext(Class classaction,Object classobj,View context){
//        try {
//            Field view_set=classaction.getSuperclass().getDeclaredField("view_IF");
//            view_set.setAccessible(true);
//            view_set.set(classobj,context);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        try {
//            Field view_set=classaction.getSuperclass().getDeclaredField("view_IF");
//            view_set.setAccessible(true);
//            view_set.set(classobj,context);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    public static void bind(Class classaction, Activity context, Object classobj){//设置view
//        Field[] ms = classaction.getDeclaredFields();
//        if (classaction.isAnnotationPresent(setContext.class)){
//            setcontext(classaction,classobj,context);
//        }else if(classaction.getSuperclass().isAnnotationPresent(setContext.class)){
//            setcontext(classaction,classobj,context);
//        }
//        for (Field m : ms) {
//            if (m.isAnnotationPresent(ViewSet.class)) {
//                ViewSet d1=m.getAnnotation(ViewSet.class);
//                m.setAccessible(true);
//                try {
//                    if(d1.isiv()){
//                        Glide.with(context).load(m.get(classobj)).into((ImageView) context.findViewById(d1.id()));
//                    }else{
//                        ((TextView)context.findViewById(d1.id())).setText(d1.head()+m.get(classobj)+d1.foot());
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//    public static void bind(Class classaction, View view, Object classobj){//设置view
//        Field[] ms = classaction.getDeclaredFields();
//        if (classaction.isAnnotationPresent(setContext.class)){
//            setcontext(classaction,classobj,view);
//        }else if(classaction.getSuperclass().isAnnotationPresent(setContext.class)){
//            setcontext(classaction,classobj,view);
//        }
//        for (Field m : ms) {
//            //Log.e("查找shezhi注解","ss");
//            if (m.isAnnotationPresent(ViewSet.class)) {
//                ViewSet d1=m.getAnnotation(ViewSet.class);
//                //Log.e("设置注解","ss");
//                m.setAccessible(true);
//                if (d1.isiv()) {
//                    try {
//                        Glide.with(view.getContext()).load(m.get(classobj)).into((ImageView) view.findViewById(d1.id()));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    try {
//                        ((TextView)view.findViewById(d1.id())).setText(d1.head()+m.get(classobj)+d1.foot());
//                    } catch (Exception e) {
//                        //Log.e("设置注解","ss"+e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//    public static void setview(Class classaction, View view, Object classobj){//设置点击事件 以及 view的逻辑操作
//        Method[] ms = classaction.getDeclaredMethods();
//
//        if (classaction.isAnnotationPresent(setContext.class)){
//            setcontext(classaction,classobj,view);
//        }else if(classaction.getSuperclass().isAnnotationPresent(setContext.class)){
//            setcontext(classaction,classobj,view);
//        }
//
//        for (Method m : ms) {
//            //Log.e("查找点击注解","ss");
//            if(m.isAnnotationPresent(viewonclick.class)){
//                //Log.e("点击注解","ss");
//                viewonclick d1=m.getAnnotation(viewonclick.class);
//                m.setAccessible(true);
//                view.findViewById(d1.value()).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //Log.e("点击了","ss");
//                        try {
//                            m.invoke(classobj);
//                        } catch (IllegalAccessException | InvocationTargetException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }else if(m.isAnnotationPresent(DoSomething_logic.class)){
//                DoSomething_logic d1=m.getAnnotation(DoSomething_logic.class);
//                m.setAccessible(true);
//                try {
//                    m.invoke(classobj);
//                } catch (IllegalAccessException | InvocationTargetException ex) {
//                    try {
//                        m.invoke(classobj,view);
//                    } catch (IllegalAccessException | InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
//                    ex.printStackTrace();
//                }
//
//            }
//        }
//    }
//
//
//    public static boolean testnull(Object m, NoNull d1,int pos){
//        Class<?> targetClass = m.getClass();
//        try {
//            if (targetClass.equals(EditText.class)) {
//                if(((EditText)m).getText()==null || ((EditText)m).getText().length()==0){
//                    ToastUtils.showShort(d1.value()[pos]);
//                    return true;
//                }else {
//                    return false;
//                }
//            }else {
//                if(m.toString().length()==0){
//                    ToastUtils.showShort(d1.value()[pos]);
//                    return true;
//                }else {
//                    return false;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return true;
//    }
//
//
//    public static boolean notnull(Class classaction,  Object classobj){//非空 判断 toast
//        Field[] ms = classaction.getDeclaredFields();
//        for (Field m : ms) {
//            if (m.isAnnotationPresent(NoNull.class)) {
//                NoNull d1=m.getAnnotation(NoNull.class);
//                m.setAccessible(true);
//                try {
//                    //Log.e("field type","=="+m.getType());
//                    if(m.getType().equals(List.class)){
//                        for (int i = 0; i < ((List) m.get(classobj)).size(); i++) {
//                            if (testnull(((List) m.get(classobj)).get(i),d1,i)) {
//                                return false;
//                            }
//                        }
//                    }else{
//                        if (testnull(m.get(classobj),d1,0)) {
//                            return false;
//                        }
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                    ToastUtils.showShort(e.getMessage());
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//
//
//
//}
