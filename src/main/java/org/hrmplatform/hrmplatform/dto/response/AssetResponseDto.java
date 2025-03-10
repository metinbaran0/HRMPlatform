package org.hrmplatform.hrmplatform.dto.response;

import java.time.LocalDate;

public record AssetResponseDto(
		Long id,
		Long employeeId,
		String assetName,
		String assetType,
		String serialNumber,
		LocalDate assignedDate,
		LocalDate returnDate
		
) {
}