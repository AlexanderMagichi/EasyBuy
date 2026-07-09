package com.teamchallenge.easybuy.shop.mapper;


import com.teamchallenge.easybuy.shop.dto.shopmoderationhistory.ShopModerationHistoryDTO;
import com.teamchallenge.easybuy.shop.entity.ShopModerationHistory;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface ShopModerationHistoryMapper {

    @Mapping(source = "shop.shopId", target = "shopId")
    @Mapping(source = "moderator.id", target = "moderatorId")
    @Mapping(source = "reversedByUser.id", target = "reversedByUserId")
    @Mapping(source = "automatic", target = "automatic")
    @Mapping(source = "reversible", target = "reversible")
    ShopModerationHistoryDTO toDto(ShopModerationHistory entity);

    @Mapping(target = "moderationHistoryId", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "moderator", ignore = true)
    @Mapping(target = "reversedByUser", ignore = true)
    @Mapping(target = "reversedAt", ignore = true)
    @Mapping(target = "impactScore", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "automatic", source = "automatic")
    @Mapping(target = "reversible", source = "reversible")
    ShopModerationHistory toEntity(ShopModerationHistoryDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "moderationHistoryId", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "moderator", ignore = true)
    @Mapping(target = "reversedByUser", ignore = true)
    @Mapping(target = "reversedAt", ignore = true)
    @Mapping(target = "impactScore", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(ShopModerationHistoryDTO dto, @MappingTarget ShopModerationHistory entity);
}