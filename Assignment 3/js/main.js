const postSection = document.getElementById("postSection")
var posts = []

function doBack() {
    window.history.back();
}

function imageHandle(img, alt) {
    return `
    <picture>
        <source srcset="/img/${img}-1x.webp, /img/${img}-2x.webp 2x, /img/${img}-3x.webp 3x">
        <img class="rounded-t-lg" src="/img/${img}.jpg" alt="${alt}" width="100%" height="100%"> 
    </picture>
    `
}

function addError(error, msg = "") {
    holder = document.createElement("div")
    holder.setAttribute("class", "flex flex-col items-center p-2 bg-red-300 mb-2 rounded-xl shadow-md")
    if (error == "Offline") {
        holder.innerHTML = `<p class="font-bold underline">You are currently Offline!</p><p>Please reconnect to refresh your feed.</p><p>Last updated at:</p><p class="text-xs">${msg}</p>`
    } else {
        holder.innerHTML = `<p class="font-bold underline">Something went wrong</p><p>${msg}</p>`
    }
        
    postSection.appendChild(holder)
}

function emptyPosts() {
    postSection.innerHTML = ""
    posts = []
}

function handleDate(date) {
    newdate = new Date(Date.parse(date))
    return newdate
}