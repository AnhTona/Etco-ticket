package com.esco.etco.service.impl;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.Seat;
import com.esco.etco.entity.request.ReqSeatDTO;
import com.esco.etco.entity.response.ResSeatDTO;
import com.esco.etco.entity.response.ResSeatRecommendationDTO;
import com.esco.etco.repository.EventRepository;
import com.esco.etco.repository.SeatRepository;
import com.esco.etco.service.SeatService;
import com.esco.etco.util.constant.SeatStatusEnum;
import com.esco.etco.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    private ResSeatDTO toDto(Seat seat) {
        ResSeatDTO dto = new ResSeatDTO();
        dto.setId(seat.getId());
        dto.setSeatLabel(seat.getSeatLabel());
        dto.setZone(seat.getZone());
        dto.setPrice(seat.getPrice());
        dto.setStatus(seat.getStatus());
        if (seat.getEvent() != null) {
            dto.setEventId(seat.getEvent().getId());
        }
        dto.setCreatedAt(seat.getCreatedAt());
        dto.setUpdatedAt(seat.getUpdatedAt());
        dto.setCreatedBy(seat.getCreatedBy());
        dto.setUpdatedBy(seat.getUpdatedBy());
        return dto;
    }

    @Override
    public ResSeatDTO create(ReqSeatDTO reqSeatDTO) throws IdInvalidException {
        Event event = eventRepository.findById(reqSeatDTO.getEventId())
                .orElseThrow(() -> new IdInvalidException("Event không tồn tại với id: " + reqSeatDTO.getEventId()));

        Seat seat = new Seat();
        seat.setSeatLabel(reqSeatDTO.getSeatLabel());
        seat.setZone(reqSeatDTO.getZone());
        seat.setPrice(reqSeatDTO.getPrice());
        seat.setStatus(reqSeatDTO.getStatus());
        seat.setEvent(event);
        return toDto(seatRepository.save(seat));
    }

    @Override
    public ResSeatDTO update(long id, ReqSeatDTO reqSeatDTO) throws IdInvalidException {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Seat không tồn tại với id: " + id));
        Event event = eventRepository.findById(reqSeatDTO.getEventId())
                .orElseThrow(() -> new IdInvalidException("Event không tồn tại với id: " + reqSeatDTO.getEventId()));

        seat.setSeatLabel(reqSeatDTO.getSeatLabel());
        seat.setZone(reqSeatDTO.getZone());
        seat.setPrice(reqSeatDTO.getPrice());
        seat.setStatus(reqSeatDTO.getStatus());
        seat.setEvent(event);
        return toDto(seatRepository.save(seat));
    }

    @Override
    public void delete(long id) throws IdInvalidException {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Seat không tồn tại với id: " + id));
        seatRepository.delete(seat);
    }

    @Override
    public ResSeatDTO getById(long id) throws IdInvalidException {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Seat không tồn tại với id: " + id));
        return toDto(seat);
    }

    @Override
    public List<ResSeatDTO> getByEventId(long eventId) {
        return seatRepository.findByEventId(eventId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ResSeatRecommendationDTO getRecommendedSeats(long eventId, List<String> selectedSeatLabels) {
        ResSeatRecommendationDTO result = new ResSeatRecommendationDTO();
        result.setHasRecommendation(false);
        result.setWarning(false);
        result.setData(new ArrayList<>());
        
        if (selectedSeatLabels == null || selectedSeatLabels.isEmpty()) {
            return result;
        }

        // Lấy tất cả ghế của sự kiện
        List<Seat> allSeats = seatRepository.findByEventId(eventId);
        
        // Lọc ra các ghế CÒN TRỐNG và CÙNG KHU VỰC (Zone) với ghế đầu tiên được chọn
        String firstSelectedLabel = selectedSeatLabels.get(0);
        Seat firstSelectedSeat = allSeats.stream()
                .filter(s -> s.getSeatLabel().equals(firstSelectedLabel))
                .findFirst()
                .orElse(null);
                
        if (firstSelectedSeat == null) return result;
        
        String targetZone = firstSelectedSeat.getZone();
        
        List<Seat> availableSeats = allSeats.stream()
                .filter(s -> s.getStatus() == SeatStatusEnum.AVAILABLE)
                .filter(s -> targetZone.equals(s.getZone()))
                .collect(Collectors.toList());

        List<Seat> recommendedSeats = new ArrayList<>();

        // Logic 1: Gợi ý ghế trống nằm chen giữa các ghế đã chọn
        // (Nếu khách chọn A1, A3 -> Gợi ý A2)
        for (String selectedLabel : selectedSeatLabels) {
            String row = selectedLabel.replaceAll("[0-9]", "");
            String colStr = selectedLabel.replaceAll("[^0-9]", "");
            
            if (row.isEmpty() || colStr.isEmpty()) continue;
            int col = Integer.parseInt(colStr);
            
            String leftSeatLabel = row + (col - 1);
            String rightSeatLabel = row + (col + 1);
            
            // Nếu khách đã chọn A1 và A3, mà A2 đang trống, thì ưu tiên cực cao việc gợi ý A2
            if (selectedSeatLabels.contains(row + (col - 2)) && !selectedSeatLabels.contains(leftSeatLabel)) {
                Seat middleSeat = availableSeats.stream().filter(s -> s.getSeatLabel().equals(leftSeatLabel)).findFirst().orElse(null);
                if (middleSeat != null && !recommendedSeats.contains(middleSeat)) {
                    recommendedSeats.add(middleSeat);
                }
            }
            if (selectedSeatLabels.contains(row + (col + 2)) && !selectedSeatLabels.contains(rightSeatLabel)) {
                 Seat middleSeat = availableSeats.stream().filter(s -> s.getSeatLabel().equals(rightSeatLabel)).findFirst().orElse(null);
                 if (middleSeat != null && !recommendedSeats.contains(middleSeat)) {
                     recommendedSeats.add(middleSeat);
                 }
            }
        }
        
        if (!recommendedSeats.isEmpty()) {
             result.setHasRecommendation(true);
             result.setWarning(true); // Đây là trường hợp kẹp giữa ghế đang chọn, bắt buộc lấy
             List<String> labels = recommendedSeats.stream().map(Seat::getSeatLabel).toList();
             result.setMessage("⚠️ Cảnh báo: Vui lòng không để trống 1 ghế lẻ (" + String.join(", ", labels) + ") ở giữa các ghế đã chọn!");
             result.setData(recommendedSeats.stream().map(this::toDto).collect(Collectors.toList()));
             return result;
        }

        // Logic 2: Cảnh báo "Chừa ghế trống vô duyên" (A1, A2, A3 có người ngồi, khách chọn A5, A6 -> Cảnh báo chừa A4)
        // Tìm các ghế ĐÃ CÓ NGƯỜI NGỒI (hoặc ĐANG CHỌN)
        List<String> occupiedLabels = allSeats.stream()
                .filter(s -> s.getStatus() != SeatStatusEnum.AVAILABLE && s.getZone().equals(targetZone))
                .map(Seat::getSeatLabel)
                .collect(Collectors.toList());
        occupiedLabels.addAll(selectedSeatLabels);

        for (String selectedLabel : selectedSeatLabels) {
            String row = selectedLabel.replaceAll("[0-9]", "");
            String colStr = selectedLabel.replaceAll("[^0-9]", "");
            if (row.isEmpty() || colStr.isEmpty()) continue;
            int col = Integer.parseInt(colStr);

            // Kiểm tra bên trái (Nếu A5 đang chọn)
            String leftSeat1 = row + (col - 1); // A4 (Ghế trống)
            String leftSeat2 = row + (col - 2); // A3 (Ghế đã có người)
            
            if (!occupiedLabels.contains(leftSeat1) && occupiedLabels.contains(leftSeat2)) {
                // A4 trống, A3 có người -> Bị lỗi chừa 1 ghế
                 Seat orphanSeat = availableSeats.stream().filter(s -> s.getSeatLabel().equals(leftSeat1)).findFirst().orElse(null);
                 if (orphanSeat != null && !recommendedSeats.contains(orphanSeat)) {
                     recommendedSeats.add(orphanSeat);
                 }
            }

            // Kiểm tra bên phải
            String rightSeat1 = row + (col + 1); 
            String rightSeat2 = row + (col + 2); 
            
            if (!occupiedLabels.contains(rightSeat1) && occupiedLabels.contains(rightSeat2)) {
                 Seat orphanSeat = availableSeats.stream().filter(s -> s.getSeatLabel().equals(rightSeat1)).findFirst().orElse(null);
                 if (orphanSeat != null && !recommendedSeats.contains(orphanSeat)) {
                     recommendedSeats.add(orphanSeat);
                 }
            }
        }
        
        if (!recommendedSeats.isEmpty()) {
             result.setHasRecommendation(true);
             result.setWarning(true); 
             List<String> labels = recommendedSeats.stream().map(Seat::getSeatLabel).toList();
             result.setMessage("⚠️ Cảnh báo: Vui lòng không để trống 1 ghế lẻ (" + String.join(", ", labels) + ") cạnh các ghế đã có người ngồi!");
             result.setData(recommendedSeats.stream().map(this::toDto).collect(Collectors.toList()));
             return result;
        }

        // Logic 3: Gợi ý ghế kế bên bình thường nếu chưa có ghế nào ở trên
        for (String selectedLabel : selectedSeatLabels) {
            String row = selectedLabel.replaceAll("[0-9]", "");
            String colStr = selectedLabel.replaceAll("[^0-9]", "");
            if (row.isEmpty() || colStr.isEmpty()) continue;
            int col = Integer.parseInt(colStr);
            
            String leftSeatLabel = row + (col - 1);
            String rightSeatLabel = row + (col + 1);
            
            for (Seat seat : availableSeats) {
                if (!recommendedSeats.contains(seat) && !selectedSeatLabels.contains(seat.getSeatLabel())) {
                    if (seat.getSeatLabel().equals(leftSeatLabel) || seat.getSeatLabel().equals(rightSeatLabel)) {
                        recommendedSeats.add(seat);
                    }
                }
            }
        }
        
        if (!recommendedSeats.isEmpty()) {
             List<Seat> finalRecommendations = recommendedSeats.stream().limit(3).collect(Collectors.toList());
             result.setHasRecommendation(true);
             result.setWarning(false);
             List<String> labels = finalRecommendations.stream().map(Seat::getSeatLabel).toList();
             result.setMessage("Gợi ý: Bạn có muốn đặt thêm các ghế kế bên (" + String.join(", ", labels) + ") để ngồi chung với bạn bè không?");
             result.setData(finalRecommendations.stream().map(this::toDto).collect(Collectors.toList()));
             return result;
        }

        return result;
    }
}
