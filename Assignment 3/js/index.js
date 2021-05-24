function addPost(id, title, description, likes, img) {
    holder = document.createElement("a")
    holder.setAttribute("class", "flex flex-col bg-gray-400 dark:bg-gray-700 h-auto mb-2 shadow-xl rounded-lg")
    holder.setAttribute("id", id)
    holder.setAttribute("href", `javascript:showPost(${id})`)
    holder.innerHTML = `
    ${imageHandle(img, description)}
    <div class="flex p-2 pt-1">
        <div class="flex flex-col pr-0 flex-1">
            <h1 class="text-gray-800 font-bold text-2xl dark:text-white">
                ${title}
            </h1>
            <p class="text-gray-700 text-sm dark:text-gray-300">
                ${description}
            </p>
        </div>
        <p class="row-span-2 text-gray-700 text-right my-auto text-sm dark:text-gray-300">
            ${likes}<i class="far fa-thumbs-up pl-2">
            </i>
        </p>
    </div>
    `

    postSection.appendChild(holder)
}

function showPost(id) {
    sessionStorage.setItem("displayPost", id)
    window.location.href = "post.html"
}

function getPosts(url = "https://api.comp50011.uni.aylo.net/posts") {
    fetch(url)
        .then(function (response) {
            return response.json()
        })
        .then(function (data) {
            data.forEach(post => posts.push(post))
            localStorage.setItem("cachedPosts", JSON.stringify(posts))
            localStorage.setItem("cacheUpdateAt", new Date())
            renderPosts()
        })
        .catch(function (error) {
            addError("Other", error)
        })
}

function refreshFeed() {
    emptyPosts()
    handlePosts()
}

function renderPosts() {
    for(var i = 0; i < posts.length; i++) {
        let post = posts[i]
        addPost(post.id, post.title, post.description, post.likes, post.image.slice(0, -4))
    }
}

function handlePosts() {
    if (navigator.onLine) {
        getPosts()
    } else {
        addError("Offline", handleDate(localStorage.getItem("cacheUpdateAt")))
        posts = JSON.parse(localStorage.getItem("cachedPosts"))
        renderPosts()
    }
}

window.addEventListener('load', function() {
    handlePosts()
}, false)