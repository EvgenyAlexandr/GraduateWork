package ru.skypro.homework.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;

/**
 * Unit-тесты {@link ru.skypro.homework.mapper.AdMapper}.
 * <p>
 * Проверяют корректность MapStruct-преобразований без Spring-контекста.
 */
class AdMapperTest {

    private AdMapper adMapper;

    @BeforeEach
    void setUp() {
        adMapper = Mappers.getMapper(AdMapper.class);
    }

    @Test
    @DisplayName("Маппинг сущности Ad в DTO сохраняет поля объявления и id автора")
    void toDto_mapsAdFields() {
        User author = new User();
        author.setId(1);

        Ad ad = new Ad();
        ad.setPk(10);
        ad.setTitle("Test ad");
        ad.setDescription("Description");
        ad.setPrice(1000);
        ad.setImage("/ads/10/image");
        ad.setAuthor(author);

        ru.skypro.homework.dto.Ad dto = adMapper.toDto(ad);

        assertThat(dto.getPk()).isEqualTo(10);
        assertThat(dto.getTitle()).isEqualTo("Test ad");
        assertThat(dto.getPrice()).isEqualTo(1000);
        assertThat(dto.getAuthor()).isEqualTo(1);
        assertThat(dto.getImage()).isEqualTo("/ads/10/image");
    }

    @Test
    @DisplayName("Маппинг Ad в ExtendedAd включает данные автора объявления")
    void toExtendedDto_mapsAuthorFields() {
        User author = new User();
        author.setId(1);
        author.setEmail("user@test.com");
        author.setFirstName("Ivan");
        author.setLastName("Ivanov");
        author.setPhone("+79991234567");

        Ad ad = new Ad();
        ad.setPk(10);
        ad.setTitle("Test ad");
        ad.setDescription("Description");
        ad.setPrice(1000);
        ad.setAuthor(author);

        var dto = adMapper.toExtendedDto(ad);

        assertThat(dto.getEmail()).isEqualTo("user@test.com");
        assertThat(dto.getAuthorFirstName()).isEqualTo("Ivan");
        assertThat(dto.getAuthorLastName()).isEqualTo("Ivanov");
        assertThat(dto.getPhone()).isEqualTo("+79991234567");
    }

    @Test
    @DisplayName("Маппинг CreateOrUpdateAd в сущность Ad с привязкой автора")
    void toEntity_mapsCreateOrUpdateAd() {
        User author = new User();
        author.setId(1);

        CreateOrUpdateAd createOrUpdateAd = new CreateOrUpdateAd();
        createOrUpdateAd.setTitle("New ad");
        createOrUpdateAd.setDescription("New description");
        createOrUpdateAd.setPrice(500);

        Ad ad = adMapper.toEntity(createOrUpdateAd, author);

        assertThat(ad.getTitle()).isEqualTo("New ad");
        assertThat(ad.getDescription()).isEqualTo("New description");
        assertThat(ad.getPrice()).isEqualTo(500);
        assertThat(ad.getAuthor()).isEqualTo(author);
        assertThat(ad.getPk()).isNull();
    }

    @Test
    @DisplayName("toAdsDto формирует обёртку Ads со счётчиком и списком DTO")
    void toAdsDto_mapsListWithCount() {
        User author = new User();
        author.setId(1);

        Ad ad = new Ad();
        ad.setPk(10);
        ad.setTitle("Test ad");
        ad.setDescription("Description");
        ad.setPrice(1000);
        ad.setImage("/images/ads/test.jpg");
        ad.setAuthor(author);

        var ads = adMapper.toAdsDto(java.util.List.of(ad));

        assertThat(ads.getCount()).isEqualTo(1);
        assertThat(ads.getResults()).hasSize(1);
        assertThat(ads.getResults().get(0).getPk()).isEqualTo(10);
    }
}
