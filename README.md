# 在 RecyclerView.ViewHolder 中使用 ViewBinding

```kotlin
class PhotoViewHolder(private val mBinding: GalleryCellBinding) : RecyclerView.ViewHolder(mBinding.root) {

    companion object {
        fun newInstance(parent: ViewGroup): PhotoViewHolder {
            // 获取并注入 binding 对象
            val binding = GalleryCellBinding.inflate(LayoutInflater.from(parent.context))
            return PhotoViewHolder(binding)
        }
    }

    fun bindWithPhotoItem(photoItem: PhotoItem) {
        // 通过 binding 对象初始化 ItemView
        mBinding.imageView.layoutParams.height = photoItem.photoHeight
        mBinding.textViewUser.text = photoItem.photoUser
        mBinding.textViewCollections.text = photoItem.photoCollections.toString()
        mBinding.textViewLikes.text = photoItem.photoLikes.toString()
        ...
    }
}
```

```kotlin
class GalleryAdapter(private val viewModel: GalleryViewModel) : PagedListAdapter<PhotoItem, RecyclerView.ViewHolder>(DIFFCALLBACK) {
    ...

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PhotoViewHolder.newInstance(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PhotoViewHolder).bindWithPhotoItem(photoItem)
    }
    ...
}
```

在 RecyclerView.ViewHolder 中使用 ViewBinding 需要谨慎：
- binding View 的 Layout 中，最外层用 xxx Layout 元素包裹，否则会导致部分属性例如 margin 设置无法生效。
- 业务场景不可以太复杂，例如通过 binding.layoutParams 获取 Layout 参数时会 NPE
- 不可以在 ViewPager2 绑定的 adapter 的 RecyclerView.ViewHolder 中使用 ViewBinding，同样会报和 Layout 参数相关的异常。