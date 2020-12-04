package io.github.loggerworld.dto.response

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty


@ApiModel(description = "Base response object")
data class ResponseObject<T>(
    @ApiModelProperty(notes = "True if call was successful", position = 1)
    var success: Boolean = true,
    @ApiModelProperty(notes = "Error message if call was unsuccessful", position = 2)
    var message: String = "",
    @ApiModelProperty(notes = "Payload if call was successful", position = 3)
    var data: T? = null
)
