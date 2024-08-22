//package adapter;
//
//import android.util.Log;
//import android.view.View;
//
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * 万能的适配器
// * 所有的初始化放入 bean类中处理，（@viewset @viewonclick）
// * 适用于 逻辑简单的列表展示
// *
// */
//
//public class SoQuickAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {
//
//
//    public SoQuickAdapter(int layoutResId, ArrayList<Object> data) {
//        super(layoutResId, data);
//    }
//    public SoQuickAdapter(int layoutResId, List<?> data,  View.OnClickListener click) {
//        super(layoutResId, (List<Object>) data);
//        listen=click;
//    }
//
//    Map<Integer, View.OnClickListener> viewclicks=null;
//    public SoQuickAdapter(int layoutResId, List<?> data, Map<Integer, View.OnClickListener> viewclick) {
//        super(layoutResId, (List<Object>) data);
//        viewclicks=viewclick;
//    }
//
//    View.OnClickListener listen=null;
//    @Override
//    protected void convert(BaseViewHolder helper, Object item) {
//        helper.itemView.setTag(helper.getLayoutPosition());
//        Log.e("数据类型","-"+item.getClass());
//        GuiBhelp.bind(item.getClass(),helper.itemView,item);
//        GuiBhelp.setview(item.getClass(),helper.itemView,item);
//        if(listen!=null){
//            helper.itemView.setOnClickListener(listen);
//        }
//        if(viewclicks!=null && viewclicks.size()>0){
//            for(Map.Entry<Integer, View.OnClickListener> entry : viewclicks.entrySet()) {
//                try {
//                    Integer mapKey = entry.getKey();
//                    View.OnClickListener mapValue = entry.getValue();
//                    helper.getView(mapKey).setOnClickListener(mapValue);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
