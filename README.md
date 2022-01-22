# ShimmerLayout

ShimmerLayout 用于为 PlaceHolder 设置一个加载中动画。

![](https://github.com/team-supercharge/ShimmerLayout/raw/master/shimmerlayout.gif?raw=true)

## 添加依赖
```
implementation 'io.supercharge:shimmerlayout:2.1.0'
```
## 在 UI 中使用
```xml
<io.supercharge.shimmerlayout.ShimmerLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/shimmerLayoutPhoto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".PhotoFragment" >

    <uk.co.senab.photoview.PhotoView
        android:id="@+id/photoView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:adjustViewBounds="true"
        tools:src="@tools:sample/avatars" />
</io.supercharge.shimmerlayout.ShimmerLayout>
```

## 在 Fragment UIController 中使用

```kotlin
class PhotoFragment : Fragment() {
    ...
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ...
        shimmerLayoutPhoto.apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }
        ...
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shimmerLayoutPhoto.apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }

        Glide.with(requireContext())
            ...
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    ...
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    ...
                ): Boolean {
                    return false.also { shimmerLayoutPhoto.stopShimmerAnimation() }
                }

            })
            ...
    }
}
```

## 在 RecyclerView.Adapter 中使用

```kotlin
class GalleryAdapter: ListAdapter<PhotoItem, MyViewHolder>(DIFFCALLBACK) {
    ...
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.shimmerLayoutCell.apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }

        Glide.with(holder.itemView)
            ...
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    ...
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    ...
                ): Boolean {
                    return false.also { holder.shimmerLayoutCell.stopShimmerAnimation() }
                }

            })
            ...
    }
}
```