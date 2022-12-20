[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jamshedalamqaderi/anview/badge.svg?style=plastic)](https://central.sonatype.dev/artifact/com.jamshedalamqaderi/anview/)

# AnView

![CI](https://github.com/JamshedAlamQaderi/anview/actions/workflows/ci.yml/badge.svg) ![Analyse](https://github.com/JamshedAlamQaderi/anview/actions/workflows/analyse.yml/badge.svg) ![Publish](https://github.com/JamshedAlamQaderi/anview/actions/workflows/publish.yml/badge.svg) [![CodeFactor](https://www.codefactor.io/repository/github/jamshedalamqaderi/anview/badge)](https://www.codefactor.io/repository/github/jamshedalamqaderi/anview) [![Coverage](https://codecov.io/gh/JamshedAlamQaderi/anview/branch/main/graph/badge.svg?token=AAVO2UQXLP)](https://codecov.io/gh/JamshedAlamQaderi/anview)

AnView is an android library which scrape android view with the help of Android Accessibility Service

### Setup

Add this repository to the `build.gradle.kts` dependency on your android project.

Note: Get the latest version from the Maven Central (Available on top badge) and replace with `<anview_version>`.

```gradle
implementation("com.jamshedalamqaderi:anview:<anview_version>")
```

### How to setup Accessibility service

1. Create a class and implement it with `AnViewAccessibilityService` instead of `AccessibilityService` or replace `AccessibilityService` with `AnViewAccessibilityService`

   ```kotlin
   import com.jamshedalamqaderi.anview.services.AnViewAccessibilityService

   class ExampleAccessibilityService : AnViewAccesibilityService{}
   ```
2. Follow this [docs](https://developer.android.com/guide/topics/ui/accessibility/service) to see how to setup an Accessibility Service with configurations
3. Make sure `android:canRetrieveWindowContent="true"` is `true` in the `config.xml`

## How to use

1. Retrieve view node periodically.

   ```kotlin
   import com.jamshedalamqaderi.anview.services.AnViewAccessibilityService
   import kotlin.time.Duration.Companion.seconds

   // Can be called from anywhere
   // If service is not enabled by user then nothing will be observed by this observer
   // On Every 1 second this callback function will be called with AccessibilityNodeInfo object
   AnViewAccessibilityService.registerViewObserver("UNIQUE_TAG", 1.seconds){ nodeInfo ->
     // do scrape operation here
   }

   // do not forget to remove observer from registered observers
   fun onDestroy(){
      AnViewAccessibilityService.removeViewObserver("UNIQUE_TAG")
   }

   ```
2. Debug view tree for building query dsl using top to bottom tree structure

   ```kotlin
   import com.jamshedalamqaderi.anview.ext.AccessibilityNodeInfoExt.toTreeString

   // You can get latest view node
   val nodeInfo : AccessibilityNodeInfo? = AnViewAccessibilityService.currentView()

   // show nodeInfo with properties in a tree
   // information can be used for building query dsl in next section
   println(nodeInfo?.toTreeString())

   ```
3. Create query using DSL & find View node using extension functions

   ```kotlin
   import com.jamshedalamqaderi.anview.dsl.anViewQuery
   import com.jamshedalamqaderi.anview.ext.AccessibilityNodeInfoExt.*

   val searchField = anViewQuery{
       params{
           param(ParamType.packageName, "com.jamshedalamqaderi.anview")
           param(ParamType.className, "android.widget.ViewGroup")
           param(ParamType.nodeIndex, "0") // selecting first index from same className's in the same list
       }
       query{
           params{
               param(ParamType.packageName, "com.jamshedalamqaderi.anview")
               param(ParamType.className, "android.widget.EditText")
               param(ParamType.text, "Search([a-z0-9A-Z])") // Regex is supported for some param type's
           }
       }
   }

   val matchedNode = nodeInfo?.findNode(searchField) // returns single node
   val matchedNodeSkipFirst = nodeInfo?.findNode(searchField, skip = 0) // skip first matching result
   val matchingNodes = nodeInfo?.findNodes(searchField) // return multiple matching node list
   ```
4. Using `AnViewActions`

   ```kotlin
   import com.jamshedalamqaderi.anview.ext.AnViewActions.*

   // extension functions for AccessibilityNodeInfo

   // actions are made by the help of AccessibilityNodeInfo default actions
   // you can perform more actions by using AccessibilityNodeInfo.performAction(actionId)
   matchingNodeInfo.click() // clicking on a view node
   matchingNodeInfo.longClick() // long click on a view node
   matchingNodeInfo.swipeForward() // scroll node content to forward
   matchingNodeInfo.swipeBackward() // scroll node content to backward
   matchingNodeInfo.inputText("Hello, there!") // write text on editable widget

   // Coordinated actions - requires api level (24+)
   // same as Accessibility actions, but human like click with coordinated positions
   matchingNodeInfo.tap() // clicking on a view node with center position
   matchingNodeInfo.longTap() // long clicking on a view node with center position
   matchingNodeInfo.swipeUp() // coordinated swipe up
   matchingNodeInfo.swipeRight() // coordinated swipe right
   matchingNodeInfo.swipeDown() // coordinated swipe down
   matchingNodeInfo.swipeLeft() // coordinated swipe left

   AnViewActions.swipe(startX, startY, endX, endY) // custom coordinated swipe function
   AnViewActions.pressBack() // simulate back press on phone
   AnViewActions.pressHome() // simulate home button press on phone
   ```

# TODO

# Author
Jamshed Alam Qaderi
jamshedalamqaderi@gmail.com

# Support
Need help