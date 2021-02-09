@extends('layouts.app')

@section('content')
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">{{ __('Dashboard') }}</div>

                    <div class="card-body">
                        <form method="post" enctype="multipart/form-data">
                            @csrf
                            <fieldset>
                                <legend>Enter your registration details</legend>

                                <div class="form-group row"> <!-- Added formatting -->
                                    <label for="title" class="col-md-4 col-form-label text-md-right">Title:</label> <!-- Added formatting -->

                                    <div class="col-md-6">
                                        <input type="text" required name="title" id="title" class="form-control @error('title') is-invalid @enderror" value="{{ old('title') }}" autofocus> <!-- Added error feedback, re-fill value on error, focus on load -->

                                        @error('title') <!-- Added error feedback -->
                                        <span class="invalid-feedback" role="alert">
                                         <strong>{{ $message }}</strong>
                                        </span>
                                        @enderror
                                    </div>
                                </div>

                                <div class="form-group row"> <!-- Added formatting -->
                                    <label for="description" class="col-md-4 col-form-label text-md-right">Description:</label> <!-- Added formatting -->

                                    <div class="col-md-6">
                                        <input type="text" required name="description" id="description" class="form-control @error('description') is-invalid @enderror" value="{{ old('description') }}" autofocus> <!-- Added error feedback, re-fill value on error, focus on load -->

                                    @error('description') <!-- Added error feedback -->
                                        <span class="invalid-feedback" role="alert">
                                         <strong>{{ $message }}</strong>
                                        </span>
                                        @enderror
                                    </div>
                                </div>

                                <div class="form-group row">
                                    <label for="image" class="col-md-4 col-form-label text-md-right">Image:</label> <!-- Added formatting -->

                                    <div class="col-md-6">
                                        <input type="file" required name="image" id="image" class="form-control @error('image') is-invalid @enderror" value="{{ old('image') }}" autofocus> <!-- Added error feedback, re-fill value on error, focus on load -->

                                    @error('image') <!-- Added error feedback -->
                                        <span class="invalid-feedback" role="alert">
                                         <strong>{{ $message }}</strong>
                                        </span>
                                        @enderror
                                    </div>
                                </div>

                                <div class="form-group row"> <!-- Added formatting -->
                                    <label for="lat" class="col-md-4 col-form-label text-md-right">Latitude:</label> <!-- Added formatting -->

                                    <div class="col-md-6">
                                        <input type="number" required name="lat" id="lat" class="form-control @error('lat') is-invalid @enderror" value="{{ old('lat') }}" autofocus> <!-- Added error feedback, re-fill value on error, focus on load -->

                                        @error('lat') <!-- Added error feedback -->
                                        <span class="invalid-feedback" role="alert">
                                         <strong>{{ $message }}</strong>
                                        </span>
                                        @enderror
                                    </div>
                                </div>

                                <div class="form-group row"> <!-- Added formatting -->
                                    <label for="lon" class="col-md-4 col-form-label text-md-right">Longitude:</label> <!-- Added formatting -->

                                    <div class="col-md-6">
                                        <input type="number" required name="lon" id="lon" class="form-control @error('lon') is-invalid @enderror" value="{{ old('lon') }}" autofocus> <!-- Added error feedback, re-fill value on error, focus on load -->

                                    @error('lon') <!-- Added error feedback -->
                                        <span class="invalid-feedback" role="alert">
                                         <strong>{{ $message }}</strong>
                                        </span>
                                        @enderror
                                    </div>
                                </div>

                                <div class="form-group row"> <!-- Added formatting -->
                                    <label for="likes" class="col-md-4 col-form-label text-md-right">Likes:</label> <!-- Added formatting -->

                                    <div class="col-md-6">
                                        <input type="number" required name="likes" id="likes" class="form-control @error('likes') is-invalid @enderror" value="{{ old('likes') }}" autofocus> <!-- Added error feedback, re-fill value on error, focus on load -->

                                    @error('likes') <!-- Added error feedback -->
                                        <span class="invalid-feedback" role="alert">
                                         <strong>{{ $message }}</strong>
                                        </span>
                                        @enderror
                                    </div>
                                </div>



                            </fieldset>
                            <div class="col-md-8 offset-md-4">
                                <button type="submit" name="submit" class="btn btn-primary" formnovalidate>Submit Details</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
