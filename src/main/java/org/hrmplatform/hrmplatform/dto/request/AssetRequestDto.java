package org.hrmplatform.hrmplatform.dto.request;

import java.time.LocalDate;

public record AssetRequestDto(
		String employeeName,
		String assetName,
		String assetType,
		String serialNumber,
		LocalDate assignedDate,
		LocalDate returnDate
) {
}