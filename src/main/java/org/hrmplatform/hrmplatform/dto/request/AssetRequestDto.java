package org.hrmplatform.hrmplatform.dto.request;

import java.time.LocalDate;

public record AssetRequestDto(
		Long employeeId,
		String assetName,
		String assetType,
		String serialNumber,
		LocalDate assignedDate,
		LocalDate returnDate
) {
}