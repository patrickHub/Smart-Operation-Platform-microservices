package ch.smart.operations.platform.workorder.api.controllers;

import ch.smart.operations.platform.workorder.api.contracts.AddWorkOrderTaskRequest;
import ch.smart.operations.platform.workorder.api.contracts.AssignTechnicianRequest;
import ch.smart.operations.platform.workorder.api.contracts.CancelWorkOrderRequest;
import ch.smart.operations.platform.workorder.api.contracts.CompleteWorkOrderRequest;
import ch.smart.operations.platform.workorder.api.contracts.CreateWorkOrderRequest;
import ch.smart.operations.platform.workorder.api.contracts.TechnicianActionRequest;
import ch.smart.operations.platform.workorder.application.commands.AddWorkOrderTaskCommand;
import ch.smart.operations.platform.workorder.application.commands.AssignTechnicianCommand;
import ch.smart.operations.platform.workorder.application.commands.CancelWorkOrderCommand;
import ch.smart.operations.platform.workorder.application.commands.CompleteWorkOrderCommand;
import ch.smart.operations.platform.workorder.application.commands.CreateWorkOrderCommand;
import ch.smart.operations.platform.workorder.application.commands.TechnicianActionCommand;
import ch.smart.operations.platform.workorder.application.dtos.InterventionReportDto;
import ch.smart.operations.platform.workorder.application.dtos.WorkOrderAssignmentDto;
import ch.smart.operations.platform.workorder.application.dtos.WorkOrderDto;
import ch.smart.operations.platform.workorder.application.dtos.WorkOrderTaskDto;
import ch.smart.operations.platform.workorder.application.services.WorkOrderApplicationService;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderPriority;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/work-orders")
public class WorkOrderController {

        private final WorkOrderApplicationService workOrderApplicationService;

        public WorkOrderController(WorkOrderApplicationService workOrderApplicationService) {
                this.workOrderApplicationService = workOrderApplicationService;
        }

        @PostMapping
        public ResponseEntity<Map<String, UUID>> createWorkOrder(
                        @Valid @RequestBody CreateWorkOrderRequest request) {
                UUID id = workOrderApplicationService.createWorkOrder(
                                new CreateWorkOrderCommand(
                                                request.assetId(),
                                                request.createdByUserId(),
                                                request.type(),
                                                request.priority(),
                                                request.title(),
                                                request.description(),
                                                request.requestedDate(),
                                                request.scheduledStart(),
                                                request.scheduledEnd(),
                                                request.createdBy()));

                return ResponseEntity
                                .created(URI.create("/api/v1/work-orders/" + id))
                                .body(Map.of("id", id));
        }

        @GetMapping("/{workOrderId}")
        public ResponseEntity<WorkOrderDto> getWorkOrder(@PathVariable UUID workOrderId) {
                return ResponseEntity.ok(workOrderApplicationService.getWorkOrderById(workOrderId));
        }

        @GetMapping
        public ResponseEntity<List<WorkOrderDto>> searchWorkOrders(
                        @RequestParam(required = false) WorkOrderStatus status,
                        @RequestParam(required = false) WorkOrderPriority priority,
                        @RequestParam(required = false) UUID assignedTechnicianUserId,
                        @RequestParam(required = false) UUID customerId) {
                return ResponseEntity.ok(
                                workOrderApplicationService.searchWorkOrders(
                                                status,
                                                priority,
                                                assignedTechnicianUserId,
                                                customerId));
        }

        @PostMapping("/{workOrderId}/assignments")
        public ResponseEntity<Void> assignTechnician(
                        @PathVariable UUID workOrderId,
                        @Valid @RequestBody AssignTechnicianRequest request) {
                workOrderApplicationService.assignTechnician(
                                new AssignTechnicianCommand(
                                                workOrderId,
                                                request.technicianUserId(),
                                                request.assignedByUserId(),
                                                request.updatedBy()));

                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{workOrderId}/accept")
        public ResponseEntity<Void> acceptAssignment(
                        @PathVariable UUID workOrderId,
                        @Valid @RequestBody TechnicianActionRequest request) {
                workOrderApplicationService.acceptAssignment(
                                new TechnicianActionCommand(
                                                workOrderId,
                                                request.technicianUserId(),
                                                request.updatedBy()));

                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{workOrderId}/start")
        public ResponseEntity<Void> startIntervention(
                        @PathVariable UUID workOrderId,
                        @Valid @RequestBody TechnicianActionRequest request) {
                workOrderApplicationService.startIntervention(
                                new TechnicianActionCommand(
                                                workOrderId,
                                                request.technicianUserId(),
                                                request.updatedBy()));

                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{workOrderId}/complete")
        public ResponseEntity<Void> completeWorkOrder(
                        @PathVariable UUID workOrderId,
                        @Valid @RequestBody CompleteWorkOrderRequest request) {
                workOrderApplicationService.completeWorkOrder(
                                new CompleteWorkOrderCommand(
                                                workOrderId,
                                                request.technicianUserId(),
                                                request.startedAt(),
                                                request.completedAt(),
                                                request.laborDurationMinutes(),
                                                request.summary(),
                                                request.actionsPerformed(),
                                                request.resultStatus(),
                                                request.followUpRequired(),
                                                request.followUpNotes(),
                                                request.usedParts() == null ? List.of()
                                                                : request.usedParts().stream()
                                                                                .map(part -> new CompleteWorkOrderCommand.UsedPartCommand(
                                                                                                part.partNumber(),
                                                                                                part.partName(),
                                                                                                part.quantity(),
                                                                                                part.unitPrice(),
                                                                                                part.currency()))
                                                                                .toList(),
                                                request.updatedBy()));

                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{workOrderId}/cancel")
        public ResponseEntity<Void> cancelWorkOrder(
                        @PathVariable UUID workOrderId,
                        @Valid @RequestBody CancelWorkOrderRequest request) {
                workOrderApplicationService.cancelWorkOrder(
                                new CancelWorkOrderCommand(workOrderId, request.reason(), request.updatedBy()));
                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{workOrderId}/tasks")
        public ResponseEntity<Map<String, UUID>> addTask(
                        @PathVariable UUID workOrderId,
                        @Valid @RequestBody AddWorkOrderTaskRequest request) {
                UUID id = workOrderApplicationService.addTask(
                                new AddWorkOrderTaskCommand(
                                                workOrderId,
                                                request.title(),
                                                request.description(),
                                                request.taskOrder(),
                                                request.createdBy()));

                return ResponseEntity.created(URI.create("/api/v1/work-orders/" + workOrderId + "/tasks/" + id))
                                .body(Map.of("id", id));
        }

        @GetMapping("/{workOrderId}/tasks")
        public ResponseEntity<List<WorkOrderTaskDto>> listTasks(@PathVariable UUID workOrderId) {
                return ResponseEntity.ok(workOrderApplicationService.listTasks(workOrderId));
        }

        @GetMapping("/{workOrderId}/assignments")
        public ResponseEntity<List<WorkOrderAssignmentDto>> listAssignments(@PathVariable UUID workOrderId) {
                return ResponseEntity.ok(workOrderApplicationService.listAssignments(workOrderId));
        }

        @GetMapping("/{workOrderId}/report")
        public ResponseEntity<InterventionReportDto> getReport(@PathVariable UUID workOrderId) {
                return ResponseEntity.ok(workOrderApplicationService.getInterventionReport(workOrderId));
        }
}
