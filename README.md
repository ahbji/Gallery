# ShimmerLayout

ShimmerLayout 用于为 PlaceHolder 设置一个加载中动画。

![](http://facebook.github.io/shimmer-android/images/shimmer-small.gif)

## 添加依赖
```
implementation 'com.facebook.shimmer:shimmer:0.5.0'
```
## 在 UI 中使用
```xml
<com.facebook.shimmer.ShimmerFrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
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
</com.facebook.shimmer.ShimmerFrameLayout>
```

## 在 Fragment UIController 中使用

```kotlin
class PhotoFragment : Fragment() {
    ...
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shimmerBuilder = Shimmer.ColorHighlightBuilder()
        shimmerLayoutPhoto.apply {
            setShimmer(
                shimmerBuilder
                    .setHighlightColor(0x55FFFFFF)
                    .setBaseAlpha(1f)
                    .setBaseColor(0xD9D9D9)
                    .setDropoff(0.3f)
                    .setTilt(0f)
                    .build()
            )
            startShimmer()
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
                    return false.also { shimmerLayoutPhoto.setShimmer(null) }
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
        val shimmerBuilder = Shimmer.ColorHighlightBuilder()
        holder.shimmerViewCell.apply {
            setShimmer(
                shimmerBuilder
                    .setHighlightColor(0x55FFFFFF)
                    .setBaseAlpha(1f)
                    .setBaseColor(0xD9D9D9)
                    .setDropoff(0.3f)
                    .setTilt(0f)
                    .build()
            )
            startShimmer()
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
                    return false.also { holder.shimmerViewCell.setShimmer(null) }
                }

            })
            ...
    }
}
```