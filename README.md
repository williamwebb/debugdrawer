debugdrawer
===========

HEAVILY inspired/built off of JakeWharton's awesome work in [u2020](https://github.com/JakeWharton/u2020).

        DebugDrawer.attach(this, R.layout.main);
or

        DebugDrawer.attach(this, contentView);


![](ss.png)

	// to add custom debug options        
	DebugGroup testGroup = new DebugGroup("TestGroup",this);
        testGroup.addElement(new TextElement(this,"TextElement","Value"));
        testGroup.addElement(new ToggleElement("ToggleElement",this) {
            @Override
            public void onAction(Boolean aBoolean) {
                Toast.makeText(DemoActivity.this,"Toggle: " + aBoolean,Toast.LENGTH_SHORT).show();
            }
        });
        testGroup.addElement(new SpinnerElement(this,"SpinnerElement",R.array.levels_entries) {
            @Override 
            public void onAction(String s) {
                Toast.makeText(DemoActivity.this,"Spinner: " + s,Toast.LENGTH_SHORT).show();
            }
        });
        testGroup.addElement(new DebugElement(this,"Custom") {
            @Override public void onAction(Object o) { /* nothing */ }

            @Override
            protected View createView() {
                ImageView imageView = new ImageView(DemoActivity.this);
                imageView.setImageResource(R.drawable.ic_launcher);
                return imageView;
            }
        });

        DebugDrawer.addGroup(testGroup);