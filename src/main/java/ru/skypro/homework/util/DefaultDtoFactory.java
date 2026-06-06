package ru.skypro.homework.util;

import java.util.Collections;

import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;

/**
 * Фабрика DTO с значениями по умолчанию.
 * <p>
 * Используется на Этапе I, когда контроллеры ещё не подключены к сервисному слою.
 */
public final class DefaultDtoFactory {

    private DefaultDtoFactory() {
    }

    /** @return пустой {@link User} */
    public static User emptyUser() {
        return new User();
    }

    /** @return пустой {@link UpdateUser} */
    public static UpdateUser emptyUpdateUser() {
        return new UpdateUser();
    }

    /** @return пустой {@link Ad} */
    public static Ad emptyAd() {
        return new Ad();
    }

    /**
     * @return пустой список объявлений с {@code count = 0}
     */
    public static Ads emptyAds() {
        Ads ads = new Ads();
        ads.setCount(0);
        ads.setResults(Collections.emptyList());
        return ads;
    }

    /** @return пустой {@link ExtendedAd} */
    public static ExtendedAd emptyExtendedAd() {
        return new ExtendedAd();
    }

    /** @return пустой {@link Comment} */
    public static Comment emptyComment() {
        return new Comment();
    }

    /**
     * @return пустой список комментариев с {@code count = 0}
     */
    public static Comments emptyComments() {
        Comments comments = new Comments();
        comments.setCount(0);
        comments.setResults(Collections.emptyList());
        return comments;
    }

    /** @return пустой {@link CreateOrUpdateAd} */
    public static CreateOrUpdateAd emptyCreateOrUpdateAd() {
        return new CreateOrUpdateAd();
    }

    /** @return пустой {@link CreateOrUpdateComment} */
    public static CreateOrUpdateComment emptyCreateOrUpdateComment() {
        return new CreateOrUpdateComment();
    }
}
