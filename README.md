# FlowTag

### 1.　attr
#### 1.1  max_select： 设置选中个数 
- -1： 不限制个数
-  0 ：没有选中
- \>0：设置现在个数

#### 1.2   tag_gravity：设置显示位置
- left：坐标是０的位置是左侧第一个，tag在左侧
- right：坐标是０的位置是右侧第一个，tag在右侧
- center：坐标是０的位置是左侧第一个，所有tag居中

#### 1.3  clickable： 是否可点击
- true、false

### 2 . 监听器
```java
// 选中监听器，返回选中的坐标
setOnSelectListener(listener: OnSelectListener)

// 点击监听器
setOnTagClickListener(listener: OnTagClickListener)

//达到最大数量时，监听事件
setOnBeyondMaxSelectListener(listener: OnBeyondMaxSelectListener）
```

### 3. 方法
```java
// 设置数据源
setDataList(list: List<T>)

//设置显示样式
setLayoutRes(layoutRes: Int)
setLayoutRes(layoutRes: Int,listener: OnCustomListener)

//获取选中的tag
getSelectedList()

//设置选中的tag
setSelectedList(vararg poses: Int)

//获取选中的最大个数
getMaSelectedCount()

//设置选中的最大个数
setMaxSelectCount(Int)

//设置tag是否可点击
setClicked(Boolean)
```

### 4、注
tag布局参考demo的xml样式写


