<?php

use Illuminate\Support\Facades\Route;
use App\Models\User;
use App\Models\Post;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return redirect('home');
});

Auth::routes();

Route::get('/home', [App\Http\Controllers\HomeController::class, 'index'])->name('home');

Route::get('/posts',[App\Http\Controllers\PostController::class, 'index'])->name('posts');
Route::get('/postsweb', [App\Http\Controllers\PostController::class, 'showall'])->name('showall');

Route::middleware('auth')->group(function() {
    Route::get('/post', [App\Http\Controllers\PostController::class, 'show'])->name('post');
    Route::post('/post', [App\Http\Controllers\PostController::class, 'store']);

    Route::get('/users', function() {
    return response()->json(User::all());
    });
});




