@extends('layouts.app')

@section('content')
    <div class="container">
        <div class="row justify-content-center">
            <div class="card-header">
                {{ $posts->count() }} Posts
            </div>
            <div class="col-md-8">
                @foreach($posts as $post)
                <div class="card" style="margin-top: 10px">
                    <div class="card-body">
                            <div id="{{ $post->id }}">
                                <img style="width: 100%" src="/images/{{ $post->image }}" loading="lazy">
                                <h1>{{ $post->title }}</h1>
                                <h5>{{ $post->description }}</h5>
                                {{ $post->likes }} likes
                            </div>
                    </div>
                </div>
                @endforeach
            </div>
        </div>
    </div>
@endsection
