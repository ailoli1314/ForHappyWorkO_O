# ForHappyWorkO_O
类似黄油刀，通过注解生成代码。自由配置模板代码，减少繁琐重复的代码编写，偷懒摸鱼小型app必备

    @viewonclick(R.id.txt)
    private void test1() {
		    // 设置点击事件
        Toast.makeText(MainActivity.this, "sdf", Toast.LENGTH_LONG).show();
    }
		
		@api(what = 122661)
    private void apitest(Api api) {
        // 注册api监听
    }
		
		// 设置imageView显示内容
		@ViewSet(id = R.id.txt, head = "iv",type = ViewSet.ViewType.IV)
    String dd = "url";
		
		// 设置TextView显示内容
    @ViewSet(id = R.id.txt, head = "tv",type = ViewSet.ViewType.TV)
    public String ss = "test BIND";
		
		// 更多使用方法参考：https://github.com/ailoli1314/ViewSet_helper
