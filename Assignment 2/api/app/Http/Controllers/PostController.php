<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Post;
use View;

class PostController extends Controller
{
    public function index()
    {
        return response()->json(Post::inRandomOrder()->limit(20)->orderBy('likes', 'desc')->get());
    }

    public function show()
    {
        return view('post');
    }

    public function showall()
    {
        return view::make('showall')->with('posts', Post::orderBy('likes', 'desc')->get());
    }

    public function store()
    {
        $attributes = request()->validate([
            'title' => ['required'],
            'description' => ['required'],
            'image' => ['required', 'file'],
            'lat' => ['required'],
            'lon' => ['required'],
            'likes' => ['required']
        ]);

        $attributes['image'] = str_replace("images/", "", request('image')->store('images'));

        Post::create([
            'title' => $attributes['title'],
            'description' => $attributes['description'],
            'image' => $attributes['image'],
            'lat' => $attributes['lat'],
            'lon' => $attributes['lon'],
            'likes' => $attributes['likes'],
        ]);

        return redirect("/images/" . $attributes['image']);
    }
}
