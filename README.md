# View Holder Generator
Auto generating Android View Holder classes from XML layout


This project outputs a JAR file (you can also find it [here](https://github.com/goldmank/viewholdergenerator/blob/master/bin/viewholdergenerator.jar)) that you can add to your gradle script, as a pre-build process, to automatically re-create Java classes out of layout XML.

```
Usage: viewholdergenerator.jar <package-name> <xml-file-name or layout-path> <output-path>
```

Adding to gradle:

```
apply plugin: 'com.android.application'

android {
    
    task viewholdergenerator(type: Exec, description: 'viewholdergenerator.jar') {
        commandLine 'java', '-jar', '../viewholdergenerator.jar', 'com.exampleapp', 'src/main/res/layout', 'src/main/java/com/exampleapp'
    }
    
    ... 
    
}
    
```

The tool is using the Template.java file to create the Java classes out of the layout XML. You can change it to fit the style you want.
You can also provide an implementation of CodeNamer interface to the LayoutParser to change the naming convention of the output Java classes.

# Developed By

- Kfir Goldman (goldmank@gmail.com)

# License

Copyright 2016 Kfir Goldman

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.