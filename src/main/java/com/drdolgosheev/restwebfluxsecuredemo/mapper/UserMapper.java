package com.drdolgosheev.restwebfluxsecuredemo.mapper;

import com.drdolgosheev.restwebfluxsecuredemo.dto.UserDto;
import com.drdolgosheev.restwebfluxsecuredemo.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto map(UserEntity userEntity);

    @InheritInverseConfiguration
    UserEntity map(UserDto userDto);
}
