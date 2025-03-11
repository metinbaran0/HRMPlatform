package org.hrmplatform.hrmplatform.dto.request;

import java.time.LocalDate;

public record AssetRequestDto(
		String employeeEmail,
		String assetName,
		String assetType,
		String serialNumber,
		LocalDate assignedDate,
		LocalDate returnDate
) {
}