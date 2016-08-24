package com.vhg;

public class Template {

	public static final String CODE = "package $PACKAGE_NAME$;\n\nimport android.app.Activity;\nimport android.content.Context;\nimport android.view.View;\n$IMPORT$\n\npublic class $CLASS_NAME$ {\n\n\u0009// ===========================================================\n    // Fields\n    // ===========================================================\n\n$FIELDS$\n\u0009// ===========================================================\n    // Constructor\n    // ===========================================================\n\u0009\n\u0009public $CLASS_NAME$(Activity activity) {\n\u0009\u0009this(activity.findViewById(android.R.id.content));\n\u0009}\n\u0009\n\u0009public $CLASS_NAME$(View view) {\n\u0009\u0009init(view);\n\u0009}\n\u0009\n\u0009// ===========================================================\n    // Private methods\n    // ===========================================================\n    \n    private void init(View v) {\n$INIT$\u0009}\n}\n";
}
