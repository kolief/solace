package net.solace.loader.plugins.arceuuslibrary.tree;

import lombok.Getter;
import lombok.Setter;
import net.solace.api.plugins.Task;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.GetBook;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.GetCustomerRequest;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.GetRoomBook;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.GetStamina;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.GoToBook;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.GoToCustomerArea;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.GoToGroundFloor;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.GoToTopFloor;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.HandInBook;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.HandleBreak;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.HandleStop;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.Idle;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.SolveLibrary;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.UseArcaneKnowledge;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.WalkToBank;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.WalkToLibrary;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isArcaneKnowledgeInInventory;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isAtBook;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isAtCustomer;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isAtCustomerArea;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isAtGroundFloor;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isAtLibrary;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isAtTopFloor;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isBankOpened;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isCanCollectMultipleBooks;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isCustomerBookInInventory;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isCustomerSet;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isGetRoomBook;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isGroundFloorChecked;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isLibrarySolved;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.isStaminaPresent;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.shouldHandleStop;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.shouldIdle;
import net.solace.loader.plugins.arceuuslibrary.nodes.decision.shouldStartBreak;

@Setter
@Getter
public class TreeExecutor implements Task {
    private TreeNode head;

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public int execute() {
        return head.execute();
    }

    public void init(SolaceArceuusLibrary ctx) {
        head = new shouldStartBreak(ctx)
                .setYes(new HandleBreak(ctx))
                .setNo(new shouldHandleStop(ctx)
                    .setYes(new HandleStop(ctx))
                    .setNo(new shouldIdle(ctx)
                            .setYes(new Idle(ctx))
                            .setNo(new isArcaneKnowledgeInInventory(ctx)
                                    .setYes(new UseArcaneKnowledge(ctx))
                                    .setNo(new isStaminaPresent(ctx)
                                            .setNo(new isBankOpened(ctx)
                                                    .setNo(new WalkToBank(ctx))
                                                    .setYes(new GetStamina(ctx)))
                                            .setYes(new isAtLibrary(ctx)
                                                    .setNo(new WalkToLibrary(ctx))
                                                    .setYes(new isLibrarySolved(ctx)
                                                            .setNo(new isGroundFloorChecked(ctx)
                                                                    .setNo(new isAtGroundFloor(ctx)
                                                                            .setNo(new GoToGroundFloor(ctx))
                                                                            .setYes(new SolveLibrary(ctx)))
                                                                    .setYes(new isAtTopFloor(ctx)
                                                                            .setNo(new GoToTopFloor(ctx))
                                                                            .setYes(new SolveLibrary(ctx))))
                                                            .setYes(new isCustomerSet(ctx)
                                                                    .setNo(new isAtCustomerArea(ctx)
                                                                            .setNo(new GoToCustomerArea(ctx))
                                                                            .setYes(new GetCustomerRequest(ctx)))
                                                                    .setYes(new isCustomerBookInInventory(ctx)
                                                                            .setNo(new isAtBook(ctx)
                                                                                    .setNo(new GoToBook(ctx))
                                                                                    .setYes(new isGetRoomBook(ctx)
                                                                                            .setNo(new GetBook(ctx))
                                                                                            .setYes(new GetRoomBook(ctx))))
                                                                            .setYes(new isCanCollectMultipleBooks(ctx)
                                                                                    .setNo(new isAtCustomer(ctx)
                                                                                            .setNo(new GoToCustomerArea(ctx))
                                                                                            .setYes(new HandInBook(ctx)))
                                                                                    .setYes(new isAtBook(ctx, false)
                                                                                            .setNo(new GoToBook(ctx, false))
                                                                                            .setYes(new isGetRoomBook(ctx, false)
                                                                                                    .setNo(new GetBook(ctx, false))
                                                                                                    .setYes(new GetRoomBook(ctx)))))))))))));
        }
}
