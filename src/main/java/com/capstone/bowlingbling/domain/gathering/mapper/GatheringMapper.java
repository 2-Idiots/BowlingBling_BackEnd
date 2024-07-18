package com.capstone.bowlingbling.domain.gathering.mapper;

import com.capstone.bowlingbling.domain.gathering.domain.Gathering;
import com.capstone.bowlingbling.domain.gathering.dto.GatheringDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GatheringMapper {

    GatheringMapper INSTANCE = Mappers.getMapper(GatheringMapper.class);

    @Mapping(source = "gathering.id", target = "id")
    @Mapping(source = "gathering.name", target = "name")
    @Mapping(source = "gathering.minAverage", target = "minAverage")
    @Mapping(source = "gathering.maxAverage", target = "maxAverage")
    @Mapping(source = "gathering.description", target = "description")
    @Mapping(source = "gathering.location", target = "location")
    @Mapping(source = "gathering.date", target = "date")
    @Mapping(source = "gathering.maxParticipants", target = "maxParticipants")
    @Mapping(source = "gathering.currentParticipants", target = "currentParticipants")
    GatheringDto toDto(Gathering gathering);

    @Mapping(source = "gatheringDto.id", target = "id")
    @Mapping(source = "gatheringDto.name", target = "name")
    @Mapping(source = "gatheringDto.minAverage", target = "minAverage")
    @Mapping(source = "gatheringDto.maxAverage", target = "maxAverage")
    @Mapping(source = "gatheringDto.description", target = "description")
    @Mapping(source = "gatheringDto.location", target = "location")
    @Mapping(source = "gatheringDto.date", target = "date")
    @Mapping(source = "gatheringDto.maxParticipants", target = "maxParticipants")
    @Mapping(source = "gatheringDto.currentParticipants", target = "currentParticipants")
    Gathering toEntity(GatheringDto gatheringDto);
}
