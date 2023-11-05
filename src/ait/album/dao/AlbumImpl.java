package ait.album.dao;

import ait.album.model.Photo;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

public class AlbumImpl implements Album {

    private Photo[] photos;
    private int size;
    private static Comparator<Photo> comparator = (ph1, ph2) -> {
        int res = ph1.getDate().compareTo(ph2.getDate());
        if (res == 0) {
            res = Integer.compare(ph1.getPhotoId(), ph2.getPhotoId());
        }

        return res != 0 ? res : Integer.compare(ph1.getAlbumId(), ph2.getAlbumId());
    };

    public AlbumImpl(int capacity) {
        this.photos = new Photo[capacity];
    }

    @Override
    public boolean addPhoto(Photo photo) {

        if (photo == null || this.size == photos.length
                || getPhotoFromAlbum(photo.getPhotoId(), photo.getAlbumId()) != null) {
            return false;
        }

        int index = Arrays.binarySearch(photos, 0, size, photo, comparator);

        index = index < 0 ? -index - 1 : index;

        System.arraycopy(photos, index, photos, index + 1, size - index);

        photos[index] = photo;

        size++;

        return true;
    }

    @Override
    public boolean removePhoto(int photoId, int albumId) {

        int index = -1;
        for (int i = 0; i < size; i++) {
            if (photos[i].getAlbumId() == albumId && photos[i].getPhotoId() == photoId) {
                index = i;
            }
        }

        if (index >= 0) {
            System.arraycopy(photos, index + 1, photos, index, size - index);
            size--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updatePhoto(int photoId, int albumId, String url) {

        for (int i = 0; i < size; i++) {
            if (photos[i].getAlbumId() == albumId && photos[i].getPhotoId() == photoId) {
                photos[i].setUrl(url);
                return true;
            }
        }

        return false;
    }

    @Override
    public Photo getPhotoFromAlbum(int photoId, int albumId) {

        Photo[] photo = findPhotosByPredicate(p -> p.getPhotoId() == photoId && p.getAlbumId() == albumId);
        return photo.length > 0 ? photo[0] : null;
    }

    @Override
    public Photo[] getAllPhotoFromAlbum(int albumId) {
        return findPhotosByPredicate(photo -> photo.getAlbumId() == albumId);
    }

    @Override
    public Photo[] getPhotoBetweenDate(LocalDate dateFrom, LocalDate dateTo) {

        Predicate<Photo> predicate = (photo) -> {
            int res1 = photo.getDate().toLocalDate().compareTo(dateFrom);
            int res2 = photo.getDate().toLocalDate().compareTo(dateTo);
            if (res1 >= 0 && res2 <= 0) return true;
            return false;
        };

        return findPhotosByPredicate(predicate);
    }

    @Override
    public int size() {
        return this.size;
    }

    private Photo[] findPhotosByPredicate(Predicate<Photo> predicate) {

        Photo[] tempArray = new Photo[size];
        int counter = 0;

        for (int i = 0; i < size; i++) {
            if (predicate.test(photos[i])) {
                tempArray[counter++] = photos[i];
            }
        }

        Photo[] resultPhotos = new Photo[counter];
        System.arraycopy(tempArray, 0, resultPhotos, 0, counter);

        return resultPhotos;
    }

}
